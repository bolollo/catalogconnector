<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<% 
if(request.getParameter("bbox") !=null){
	
	
	String bboxIn=request.getParameter("bbox");
	
	//bbox=1.95,42.10,2.57,42.45 
	
	//bbox=42.10 1.95,42.45 2.57
	
	String [] bbox=bboxIn.split(",");
	
	String bboxOuT=bbox[1]+" "+bbox[0]+","+bbox[3]+" "+bbox[2];
	
	String pregunta=request.getQueryString().toLowerCase();
	
	if(pregunta.indexOf("cercadatasetsclienticc")!= -1 ){
		
	pregunta=pregunta.replaceAll("do=cercadatasetsdlienticc","do=cercaDatasetsICC");	
	}
	
	pregunta=pregunta.replaceAll("bbox=","bboxno=");
	pregunta=pregunta + "&bbox="+bboxOuT;
	
	System.out.println(request.getParameter("bbox"));
	
	response.sendRedirect("wefex/client?"+pregunta);
	
}
	%>
</body>
</html>