package com.uppermac.controller;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
import com.uppermac.data.BaseResponseModel;
import com.uppermac.data.ResponseStatus;
import com.uppermac.entity.TbCompanyUser;
import com.uppermac.service.impl.TbCompanyUserServiceImpl;

@Controller
@RequestMapping("api/verification/tbScanrecode")
public class TbCompanyUserController {

	@Autowired
	private TbCompanyUserServiceImpl companyUserService;
	
	@RequestMapping("/scankey")
	public void scanUserCode(String name,String userCompany,String userCompanyId, HttpServletResponse response){
		BaseResponseModel baseResponseModel = new BaseResponseModel();
		baseResponseModel.setCode(ResponseStatus.paramsError.getCode());
		PrintWriter printWriter = null;
		try {

			response.setContentType("text/html;charset=utf-8");
			if (StringUtils.isEmpty(name)) {
				baseResponseModel.setMessage("参数name缺失");
				printWriter = response.getWriter();
				printWriter.write(JSON.toJSONString(baseResponseModel));
				printWriter.flush();
				return;
			}
			if (StringUtils.isEmpty(userCompany)) {
				baseResponseModel.setMessage("参数userCompany缺失");
				printWriter = response.getWriter();
				printWriter.write(JSON.toJSONString(baseResponseModel));
				printWriter.flush();
				return;
			}

			if (StringUtils.isEmpty(userCompanyId)) {
				baseResponseModel.setMessage("参数userCompanyId缺失");
				printWriter = response.getWriter();
				printWriter.write(JSON.toJSONString(baseResponseModel));
				printWriter.flush();
				return;
			}
			
			List<TbCompanyUser> companyUserList = companyUserService.findAll();
			if(companyUserList.size()<=0) {
				System.out.println("本地没有此员工数据");
				baseResponseModel.setCode(ResponseStatus.commonFailed.getCode());
				baseResponseModel.setMessage(ResponseStatus.commonFailed.getMessage() + "对应人员");
				printWriter = response.getWriter();
				printWriter.write(JSON.toJSONString(baseResponseModel));
				printWriter.flush();
				return;
			}
			for(TbCompanyUser tbCompanyUser:companyUserList) {
				if(tbCompanyUser.getUserName().equals(name)&&tbCompanyUser.getCompanyId()==Integer.valueOf(userCompanyId)) {
					baseResponseModel.setCode(ResponseStatus.success.getCode());
					baseResponseModel.setMessage(ResponseStatus.success.getMessage());
					printWriter = response.getWriter();
					printWriter.write(JSON.toJSONString(baseResponseModel));
					printWriter.flush();
				}
			}
		}catch (Exception e) {
			try {
				baseResponseModel.setCode(ResponseStatus.systemError.getCode());
				baseResponseModel.setMessage(ResponseStatus.systemError.getMessage());
				printWriter.write(JSON.toJSONString(baseResponseModel));
				printWriter.flush();

			} catch (Throwable throwable1) {
				if (printWriter != null) {
					printWriter.close();
				}
			}
		}finally {
			if (printWriter != null) {
				printWriter.close();
			}
		}
		
	}
}
