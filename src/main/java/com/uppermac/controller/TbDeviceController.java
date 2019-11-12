package com.uppermac.controller;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uppermac.data.ResponseStatus;
import com.uppermac.entity.responseModel.DeviceListModel;
import com.uppermac.entity.responseModel.ResponseModel;
import com.uppermac.utils.Base64;


/*	http://localhost:8080/shangweiji/api/verification/tbDevice
 * 	
 * 	大楼设备信息返回接口
 * 	依据大楼编号查找设备
 */
@Controller
public class TbDeviceController {

	
	
	String uKey = Base64.encode("admin".getBytes());
	String pKey = Base64.encode("123".getBytes());
	
	@RequestMapping("/addDevice")
	public String index(HttpServletRequest request){
		String name = (String) request.getSession().getAttribute("username");
		String password = (String) request.getSession().getAttribute("password");
		if(name == null||password == null){
			return "login";
		}
		if(name.equals(uKey)&&password.equals(pKey)) {
			return "addsb";
		}
		return "addsb";
		
	}
	
	@RequestMapping("/orgset")
	public String orgset(HttpServletRequest request){
		String name = (String) request.getSession().getAttribute("username");
		String password = (String) request.getSession().getAttribute("password");
		if(name == null||password == null){
			return "login";
		}
		if(name.equals(uKey)&&password.equals(pKey)) {
			return "orgset";
		}
		return "adorgsetdsb";
		
	}
	
	
	
	@RequestMapping("/tologin")
	public String tologin(String username,String password,HttpServletRequest request){
		
		String userNameKey = Base64.encode(username.getBytes());
		String passwordKey = Base64.encode(password.getBytes());
		if(userNameKey.equals(uKey)&&passwordKey.equals(pKey)) {
			request.getSession().setAttribute("username", userNameKey);
			request.getSession().setAttribute("password", passwordKey);
			request.getSession().setMaxInactiveInterval(10*60);
			return "orgset";
		}else {
			return "login";
		}
		
	}
	
	@RequestMapping("/savesb")
	public String savesb(HttpServletRequest request) {
		String deviceId = request.getParameter("deviceId");
		String inorout =request.getParameter("inorout");
		String reader = request.getParameter("reader");
		String deviceip =request.getParameter("sbip");
		String deviceport = request.getParameter("sbport");
		String pass ="OUT"+request.getParameter("pass");
		
		
		return "addsb";
	}
	

	/*
	@RequestMapping("/updatesb")
	public String updatesb(Model model,HttpServletRequest request){
		
		String name = (String) request.getSession().getAttribute("username");
		String password = (String) request.getSession().getAttribute("password");
		if(name == null||password == null){
			return "login";
		}
		if(name.equals(uKey)&&password.equals(pKey)) {
			model.addAttribute("tb",tbDeviceService.findAll());
			return "updatesb";
		}
		return "updatesb";
	
	}
	
	@RequestMapping("/delete")
    public String delete(String id) {
		tbDeviceService.delete(id);
        return "redirect:/updatesb";
    }
	
	@RequestMapping("/toEdit")
    public String toEdit(String id,Model model) {
		TbDevice tb = tbDeviceService.findOne(id);
		model.addAttribute("code",tb);
		
        return "edit";
    }
	
	@RequestMapping("/edit")
    public String edit(TbDevice tb) {
		//tbDeviceService.update(tb);
		System.out.println(tb.toString());
		tbDeviceService.update(tb);
        return "redirect:/updatesb";
    }
	
	@RequestMapping("/login")
	public String login(){
		return "login";
	}
	@RequestMapping("/controllerDev")
	public String controllerDev(){
		return "deviceController";
	}
	
	
	@RequestMapping(value = "api/verification/tbDevice/getDeviceLists",method = RequestMethod.POST)
	@ResponseBody
	public ResponseModel getDeviceLists(TbDevice tbDevice) {
		System.out.println(tbDevice.getOrgcode());
		ResponseModel responseModel = new ResponseModel();
		responseModel.setCode(ResponseStatus.paramsError.getCode());
		if(tbDevice.getOrgcode().isEmpty()) {
			responseModel.setMessage("参数orgcode缺失");
			return responseModel;
		}
		
		//根据大楼编号查找设备信息，并返回
		List<TbDevice> dbTbDeviceLists = tbDeviceService.findByOrgcode(tbDevice.getOrgcode());
		List<DeviceListModel> deviceListModels = new ArrayList<DeviceListModel>();
		for (TbDevice tempTbDevice : dbTbDeviceLists) {
			DeviceListModel deviceListModel = new DeviceListModel();
			deviceListModel.setDeviceName(tempTbDevice.getDevicename());
			deviceListModel.setId(tempTbDevice.getDeviceId());
			deviceListModel.setOrgCode(tempTbDevice.getOrgcode());
			deviceListModel.setTurnOver(tempTbDevice.getTurnover());
			deviceListModel.setDeviceIp(tempTbDevice.getDeviceip());
			deviceListModel.setDevicePort(tempTbDevice.getDeviceport());
			deviceListModels.add(deviceListModel);
		}
		
		//返回格式：code+message+data(List<TbDevice>)
		responseModel.setCode(ResponseStatus.success.getCode());
		responseModel.setMessage(ResponseStatus.success.getMessage());
		responseModel.setData(deviceListModels);
		return responseModel;
		
	}
	*/
	
	
}
