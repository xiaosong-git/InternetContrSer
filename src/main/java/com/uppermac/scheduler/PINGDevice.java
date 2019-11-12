package com.uppermac.scheduler;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.net.telnet.TelnetClient;
import org.java_websocket.client.WebSocketClient;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TableEvent;
import org.snmp4j.util.TableUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.uppermac.entity.Devices;
import com.uppermac.entity.PingTelnetInfo;

import com.uppermac.service.DevicesService;
import com.uppermac.service.PTinfoService;
import com.uppermac.service.TowerInforService;
import com.uppermac.utils.MyLog;

/**
 * 
 * 上位机定时PING、telnet设备，监听设备网络连通性
 * 
 * 使用SNMP监听上位机CPU，内存
 * 
 * @author Admin
 *
 */
@DisallowConcurrentExecution
public class PINGDevice implements Job {

	private MyLog logger = new MyLog(PINGDevice.class);

	@Autowired
	private TowerInforService towerInforService;

	@Autowired
	private WebSocketClient webSocketClient;
	
	@Autowired
	private PTinfoService ptinfoService;
	
	@Autowired
	private DevicesService devicesService;

	String osName = System.getProperty("os.name");

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		// TODO Auto-generated method stub
		
		
		if("3".equals(towerInforService.findNetType())) {
			logger.info("网络设置内网，无法使用Ping功能");
		}else {
			try {
				if(webSocketClient.isClosed()) {
					webSocketClient.reconnect();
				}
				ping();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void ping() throws IOException {
		List<Devices> FRdes = devicesService.findAllIPDevice();
		List<PingTelnetInfo> ptinfos = new ArrayList<>();
		String command = "";
		for (Devices faceDec : FRdes) {
			if(faceDec.getDeviceIp() ==""||faceDec.getDeviceIp() ==null) {
				logger.otherError(faceDec.getDeviceId()+"设备IP地址为空");
				continue;
			}
			if(!"SWI".equals(faceDec.getDeviceName())) {
				if (osName.contains("Linux")) {
					command = "ping " + faceDec.getDeviceIp() + " -c 4 ";
				} else {
					// windows 使用PING命令
					command = "ping " + faceDec.getDeviceIp();
				}
			}else {
				if (osName.contains("Linux")) {
					command = "ping 47.99.209.40 -c 4 ";
				} else {
					// windows 使用PING命令
					command = "ping 47.99.209.40";
				}
			}
			
			PingTelnetInfo ptinfo = initPT(command,faceDec);
			ptinfo=getDeviceInfo(ptinfo,faceDec);
			ptinfoService.save(ptinfo);
			ptinfos.add(ptinfo);
		}
		String json = JSONArray.toJSONString(ptinfos);
		logger.info(json);
		if(webSocketClient.isOpen()&&null!=json) {
			webSocketClient.send(json);
		}
		
	}
	private PingTelnetInfo initPT(String command, Devices device) {
		PingTelnetInfo ptinfo = new PingTelnetInfo();
		ptinfo.setOrgCode(towerInforService.findOrgId());
		BufferedReader br = null;
		try {
			Process p = Runtime.getRuntime().exec(command);
			new Thread().sleep(500);
			br = new BufferedReader(new InputStreamReader(p.getInputStream(), Charset.forName("GBK")));
			String line = null;
			StringBuilder sb = new StringBuilder();
			while ((line = br.readLine()) != null) {
				if (line.length() != 0)
					sb.append(line + "\r\n");
			}
			String response = sb.toString();
			if (osName.contains("Linux")) {
				ptinfo.setDeviceName(device.getDeviceName());
				ptinfo.setDeviceIP(device.getDeviceIp());
				if (!checkPINGResult(response)) {
					
					ptinfo.setPingStatus("error");
					ptinfo.setPingavg(0.0);
					ptinfo.setPingloss("");
				}else {
					ptinfo.setPingStatus("normal");
					String[] packetstr = response.split(",");
					String packetLoss = packetstr[2].replace("packet loss","").trim();
				
					ptinfo.setPingloss(packetLoss);
					int index = response.indexOf("min/avg/max/mdev");
					String str = response.substring(index);
					int startindex =str.indexOf("=");
					int endindex =str.indexOf("ms");
					String speed = str.substring(startindex+2, endindex);
					String[] s =speed.split("/");
					double pingavg = Double.parseDouble(s[1]);
					ptinfo.setPingStatus("normal");
					ptinfo.setPingavg(pingavg);
				}
				
			}else {
				ptinfo.setDeviceName(device.getDeviceName());
				ptinfo.setDeviceIP(device.getDeviceIp());
				if (!wcheckPINGResult(response)) {
					ptinfo.setPingStatus("error");
					ptinfo.setPingavg(0.0);
					ptinfo.setPingloss("");
				}else {
					// 获取PING返回值，做字符串解析
					ptinfo.setPingStatus("normal");
					logger.info("开始解析网络速度.......");
					int lastIndex = response.indexOf("最短 =");
					String lastString = response.substring(lastIndex);
					String str[] = lastString.split("，");
					String pingavgstr = str[2].substring(5,str[2].indexOf("ms"));
					double pingavg = Double.parseDouble(pingavgstr);
					ptinfo.setPingavg(pingavg);
					int packetlossindex = response.indexOf("丢失 = ");
					String packetlossString = response.substring(packetlossindex+5,packetlossindex+15);
					int loss = packetlossString.indexOf(" (");
					int loss2 = packetlossString.indexOf(" 丢失");
					String packetLoss = packetlossString.substring(loss+2,loss2);
					ptinfo.setPingloss(packetLoss);
				}
				
			}
			if(device.getDeviceName().equals("FACE")&&device.getDeviceType().equals("TPS980")) {
				TelnetClient telnetClient = new TelnetClient();
				try {
					telnetClient.connect(device.getDeviceIp(), 8080);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.otherError("telnet 异常");
				} 
				if (!telnetClient.isConnected()) {
					ptinfo.setTelStatus("error");
				}else {
					ptinfo.setTelStatus("normal");
					telnetClient.disconnect();
				}
			}else {
				ptinfo.setTelStatus("");
			}
			ptinfo.setFreshTime(getDateTime());

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return ptinfo;

	}

	// 正则表达式判断是否PING通
	private boolean checkPINGResult(String line) {
		// Pattern pattern = Pattern.compile("(\\d+ms)(\\s+)(TTL=\\d+)",
		// Pattern.CASE_INSENSITIVE);

		Pattern pattern = Pattern.compile("(TTL=\\d+)", Pattern.CASE_INSENSITIVE);
		Pattern pattern2 = Pattern.compile("(time=\\d+)", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(line);
		Matcher matcher2 = pattern2.matcher(line);
		while (matcher.find() && matcher2.find()) {
			return true;
		}
		return false;
	}
	private boolean wcheckPINGResult(String line) {
		// Pattern pattern = Pattern.compile("(\\d+ms)(\\s+)(TTL=\\d+)",
		// Pattern.CASE_INSENSITIVE);

		Pattern pattern = Pattern.compile("(TTL=\\d+)", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(line);
		while (matcher.find()) {
			return true;
		}
		return false;
	}

	private String getDateTime() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
		String date = df.format(new Date()); // new Date()为获取当前系统时间
		return date;
	}
	
	public PingTelnetInfo getDeviceInfo(PingTelnetInfo ptinfo,Devices device) throws IOException {
		//设备IP
		if(osName.contains("Linux")) {
			if(device.getDeviceName().equals("SWI")) {
				
				ptinfo.setMemory(memoryUsage());
				logger.info("内存使用率:"+memoryUsage());
				ptinfo.setCpu(cpuUsage());
				logger.info("CPU使用率:"+cpuUsage());
			}
			else {
				ptinfo.setMemory("");
				ptinfo.setCpu("");
			}
		}else {
			if(device.getDeviceName().equals("SWI")) {
				TransportMapping transport = null;
				Snmp snmp = null;
				CommunityTarget target = null;

				transport = new DefaultUdpTransportMapping();
				snmp = new Snmp(transport);// 创建snmp
				snmp.listen();// 监听消息
				target = new CommunityTarget();
				target.setCommunity(new OctetString("public"));// 社区名称
				target.setRetries(3);
				target.setAddress(GenericAddress.parse("udp:127.0.0.1/161"));
				target.setTimeout(8000);
				target.setVersion(SnmpConstants.version2c);
				String memory = collectMemory(snmp, target);
				logger.info("内存使用率:"+memory);
				ptinfo.setMemory(memory);
				String cpu = collectCPU(snmp, target);
				ptinfo.setCpu(cpu);
				logger.info("CPU使用率:"+cpu);
			}else {
				ptinfo.setMemory("");
				ptinfo.setCpu("");
			}
		}
		
		
		
		return ptinfo;
	}
	
	
	public static String collectMemory(Snmp snmp, CommunityTarget target) {
		String memory = null;
		String[] oids = { "1.3.6.1.2.1.25.2.3.1.2", // type 存储单元类型
				"1.3.6.1.2.1.25.2.3.1.3", // descr
				"1.3.6.1.2.1.25.2.3.1.4", // unit 存储单元大小
				"1.3.6.1.2.1.25.2.3.1.5", // size 总存储单元数
				"1.3.6.1.2.1.25.2.3.1.6" }; // used 使用存储单元数;
		String PHYSICAL_MEMORY_OID = "1.3.6.1.2.1.25.2.1.2";// 物理存储
		try {
			TableUtils tableUtils = new TableUtils(snmp, new DefaultPDUFactory(PDU.GETBULK));
			OID[] columns = new OID[oids.length];
			for (int i = 0; i < oids.length; i++)
				columns[i] = new OID(oids[i]);
			List<TableEvent> list = tableUtils.getTable(target, columns, null, null);
			for (TableEvent event : list) {
				VariableBinding[] values = event.getColumns();
				if (values == null)
					continue;
				int unit = Integer.parseInt(values[2].getVariable().toString());// unit 存储单元大小
				int totalSize = Integer.parseInt(values[3].getVariable().toString());// size 总存储单元数
				int usedSize = Integer.parseInt(values[4].getVariable().toString());// used 使用存储单元数
				String oid = values[0].getVariable().toString();
				if (PHYSICAL_MEMORY_OID.equals(oid)) {
					memory = (long) usedSize * 100 / totalSize + "%";
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return memory;
	}

	// 获取cpu使用率
	public static String collectCPU(Snmp snmp, CommunityTarget target) {
		String cpu = null;
		String[] oids = { "1.3.6.1.2.1.25.3.3.1.2" };
		try {
			TableUtils tableUtils = new TableUtils(snmp, new DefaultPDUFactory(PDU.GETBULK));
			OID[] columns = new OID[oids.length];
			for (int i = 0; i < oids.length; i++)
				columns[i] = new OID(oids[i]);
			List<TableEvent> list = tableUtils.getTable(target, columns, null, null);
			int percentage = 0;
			for (TableEvent event : list) {
				VariableBinding[] values = event.getColumns();
				if (values != null)
					percentage += Integer.parseInt(values[0].getVariable().toString());
			}
			cpu = percentage / list.size() + "%";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cpu;
	}
	/**
	 * 功能：获取Linux系统cpu使用率
	 */
	public String cpuUsage() {
		try {
			Map<?, ?> map1 = cpuinfo();
			System.out.println(map1.toString());
			Thread.sleep(1 * 1000);
			Map<?, ?> map2 = cpuinfo();
			System.out.println(map2.toString());

			// CPU时间=user+system+nice+idle+iowait+irq+softirq
			long user1 = Long.parseLong(map1.get("user").toString());
			long nice1 = Long.parseLong(map1.get("nice").toString());
			long system1 = Long.parseLong(map1.get("system").toString());
			long idle1 = Long.parseLong(map1.get("idle").toString());
			long iowait1 = Long.parseLong(map1.get("iowait").toString());
			long irq1 = Long.parseLong(map1.get("irq").toString());
			long softirq1 = Long.parseLong(map1.get("softirq").toString());

			long user2 = Long.parseLong(map2.get("user").toString());
			long nice2 = Long.parseLong(map2.get("nice").toString());
			long system2 = Long.parseLong(map2.get("system").toString());
			long idle2 = Long.parseLong(map2.get("idle").toString());
			long iowait2 = Long.parseLong(map2.get("iowait").toString());
			long irq2 = Long.parseLong(map2.get("irq").toString());
			long softirq2 = Long.parseLong(map2.get("softirq").toString());

			float totalidle = idle2 - idle1;
			System.out.println("totalidle:" + totalidle);

			long totalcpu1 = user1 + nice1 + system1 + idle1 + iowait1 + irq1 + softirq1;
			long totalcpu2 = user2 + nice2 + system2 + idle2 + iowait2 + irq2 + softirq2;
			float totalcpu = totalcpu2 - totalcpu1;
			System.out.println("totalcpu:" + totalcpu);
			float cpusage = 1 - (totalidle / totalcpu);
			System.out.println("cpusage:" + cpusage);
			int cpu = (int)(cpusage * 10000);
			System.out.println(cpu);
			DecimalFormat df=new DecimalFormat("0.00");
			
			return df.format((float)(cpu/100.0));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
     * 功能：CPU使用信息
     * */
	public Map<?, ?> cpuinfo() {
        InputStreamReader inputs = null;
        BufferedReader buffer = null;
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            inputs = new InputStreamReader(new FileInputStream("/proc/stat"));
            buffer = new BufferedReader(inputs);
            String line = "";
            while (true) {
                line = buffer.readLine();
                if (line == null) {
                    break;
                }
                if (line.startsWith("cpu")) {
                    StringTokenizer tokenizer = new StringTokenizer(line);
                    List<String> temp = new ArrayList<String>();
                    while (tokenizer.hasMoreElements()) {
                        String value = tokenizer.nextToken();
                        temp.add(value);
                    }
                    map.put("user", temp.get(1));
                    map.put("nice", temp.get(2));
                    map.put("system", temp.get(3));
                    map.put("idle", temp.get(4));
                    map.put("iowait", temp.get(5));
                    map.put("irq", temp.get(6));
                    map.put("softirq", temp.get(7));
                    map.put("stealstolen", temp.get(8));
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                buffer.close();
                inputs.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return map;
    }
	/**
     * 功能：内存使用率
     * */
	public String memoryUsage() {
        Map<String, Object> map = new HashMap<String, Object>();
        InputStreamReader inputs = null;
        BufferedReader buffer = null;
        try {
            inputs = new InputStreamReader(new FileInputStream("/proc/meminfo"));
            buffer = new BufferedReader(inputs);
            String line = "";
            while (true) {
                line = buffer.readLine();
                if (line == null)
                    break;
                int beginIndex = 0;
                int endIndex = line.indexOf(":");
                if (endIndex != -1) {
                    String key = line.substring(beginIndex, endIndex);
                    beginIndex = endIndex + 1;
                    endIndex = line.length();
                    String memory = line.substring(beginIndex, endIndex);
                    String value = memory.replace("kB", "").trim();
                    map.put(key, value);
                }
            }

            long memTotal = Long.parseLong(map.get("MemTotal").toString());
            long memFree = Long.parseLong(map.get("MemFree").toString());
            long memused = memTotal - memFree;
            long buffers = Long.parseLong(map.get("Buffers").toString());
            long cached = Long.parseLong(map.get("Cached").toString());

            double usage = (double) (memused - buffers - cached) / memTotal * 100;
            usage =  (double) Math.round(usage * 100) / 100;
            return  String.valueOf(usage);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                buffer.close();
                inputs.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return null;
    }
}
