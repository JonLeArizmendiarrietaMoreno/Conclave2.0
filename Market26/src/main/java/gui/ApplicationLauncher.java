package gui;

import java.awt.Color;



import java.net.URL;
import java.util.Locale;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import configuration.ConfigXML;
import dataAccess.DataAccess;
import businessLogic.BLFacade;
import businessLogic.BLFacadeImplementation;


import gui.*;

public class ApplicationLauncher { 
	
	
	public static void main(String[] args) {

		ConfigXML c=ConfigXML.getInstance();		
		Locale.setDefault(new Locale(c.getLocale()));
		
		MenuGUI a=new MenuGUI();
		a.setVisible(true);
		
		

		try {
			
			BLFacade blfcade;
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
			
			if (c.isBusinessLogicLocal()) {
				DataAccess da= new DataAccess();
				blfcade=new BLFacadeImplementation(da);
			}
			else { //If remote
				
				 String serviceName= "http://"+c.getBusinessLogicNode() +":"+ c.getBusinessLogicPort()+"/ws/"+c.getBusinessLogicName()+"?wsdl";	 
				 URL url = new URL(serviceName);

		 
		        //1st argument refers to wsdl document above
				//2nd argument is service name, refer to wsdl document above
		        QName qname = new QName("http://businessLogic/", "BLFacadeImplementationService");
		 
		        Service service = Service.create(url, qname);

		        blfcade = service.getPort(BLFacade.class);
			} 
			
			
			MenuGUI.setBussinessLogic(blfcade);

			//ErreklamatuGUI g=new ErreklamatuGUI();
			//g.setVisible(true);
			
		}catch (Exception e) {
			
			System.out.println("Error in ApplicationLauncher: "+e.toString());
		}


	}

}
