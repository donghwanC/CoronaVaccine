package com.api.vaccine;

import java.io.BufferedReader;

import java.io.InputStreamReader;

import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.api.vo.VaccVO;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		logger.info("Welcome home! The client locale is {}.", locale);
		
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		
		String formattedDate = dateFormat.format(date);
		
		model.addAttribute("serverTime", formattedDate );
		
		return "home";
	}
	
	@RequestMapping(value = "/test", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> test(HttpServletRequest request, HttpServletResponse response) throws Exception{
		logger.info("Test");
		
		request.setCharacterEncoding("utf-8");
		response.setContentType("application/json; charset=UTF-8");
		
		String addr = "https://api.odcloud.kr/api/15077603/v1/uddi:4359f130-cef0-4742-8392-cf9e76865269";
		addr += "?" + "page=1";
		addr += "&" + "perPage=1";
		addr += "&" + "serviceKey=opCBSuydsumEXL0EUAg91pJgJu5jRHYuq521Mxb3od%2BTFqxYLedQdcckUP2Ko3bupABG9lgDvWXIKBe9JNa%2BNw%3D%3D";
			
		URL url = new URL(addr);
		
		String line = "";
		String result = "";
		
		BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
		
		while((line=br.readLine())!=null) {
			result = result.concat(line);
			//System.out.println(result);
		}
		
		// json parser??? ????????? ????????? ???????????? ?????????
		JSONParser parser = new JSONParser();
		JSONObject obj = (JSONObject) parser.parse(result);
		
		// ??? ??????.. ?????? page ????????? ???????????? ?????? ??????
		int totalCount = Integer.parseInt(String.valueOf(obj.get("totalCount")));	
		//System.out.println(totalCount);
		
		/*=======    ??????????????? api??? ??? ?????? ?????? ???????????? ?????? ??????.. totalCount    ========*/
		/*=======    ??????????????? api????????? ???????????? ????????? ???????????????    ========*/
		
		int pageNum = (int) Math.ceil((double)totalCount / 100);
		//System.out.println(pageNum);
		

		VaccVO[] ar = new VaccVO[totalCount];
		int arNo = 0;
		
		for(int i=1; i<=pageNum; i++) {
			String totalAddr = "https://api.odcloud.kr/api/15077603/v1/uddi:4359f130-cef0-4742-8392-cf9e76865269";
			totalAddr += "?" + "page=" + i;
			totalAddr += "&" + "perPage=100";
			totalAddr += "&" + "serviceKey=opCBSuydsumEXL0EUAg91pJgJu5jRHYuq521Mxb3od%2BTFqxYLedQdcckUP2Ko3bupABG9lgDvWXIKBe9JNa%2BNw%3D%3D";
			
			URL totalUrl = new URL(totalAddr);
			
			String totalLine = "";
			String totalResult = "";
			
			BufferedReader totalBr = new BufferedReader(new InputStreamReader(totalUrl.openStream()));
			
			while((totalLine=totalBr.readLine())!=null) {
				totalResult = totalResult.concat(totalLine);
				//System.out.println(i+"??????: "+totalResult);
			}
			
			JSONParser totalParser = new JSONParser();
			JSONObject totalObj = (JSONObject) totalParser.parse(totalResult);
			
			// "data"??? ??????????????? ??????..
			// ????????? ???????????? json??????
			JSONArray parse_dataArr = (JSONArray) totalObj.get("data");
			
			for(int y=0; y<parse_dataArr.size(); y++) {
				JSONObject data = (JSONObject) parse_dataArr.get(y);
				VaccVO vo = new VaccVO();
				
				vo.setCallNum((String) data.get("?????????????????????"));
				vo.setCenterName((String) data.get("?????????"));
				vo.setLocation((String) data.get("??????"));
				vo.setOperName((String) data.get("????????????"));
				
				ar[arNo++] = vo;				
			}
		}
		
		// json??? ????????? map ??????
		Map<String, Object>map = new HashMap<String, Object>();
		
		map.put("ar", ar);
		
		//System.out.println(ar.length);
		
		return map;

	}
	
}
