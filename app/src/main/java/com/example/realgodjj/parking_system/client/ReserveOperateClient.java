package com.example.realgodjj.parking_system.client;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;

public class ReserveOperateClient {

    public static String reserve(String url, String userName, String parkUid) {
        String isSuccess = "";
        ClientResource client = new ClientResource(url + "/reserve");
        try {
            Form form = new Form();
            //传递用户名参数是为了能够创建用户预订记录的接口
            form.add("userName", userName);
            form.add("parkUid", parkUid);
            System.out.println("\n\n\n....................................................reserve hahahahahahhaha\n\n\n");
            Representation representation = form.getWebRepresentation();
            String text = representation.getText();
            StringRepresentation stringRepresentation = new StringRepresentation(text, MediaType.ALL);
            System.out.println("User reserve " + text);
            isSuccess = client.put(stringRepresentation).getText();

            System.out.println("\n\n\nreserve hahahahahahhaha....................................................\n\n\n");

            System.out.println("User reserve " + "{" + isSuccess + "}");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

    public static String undoReserve(String url, String userName, String parkUid) {
        String isSuccess = "";
        ClientResource client = new ClientResource(url + "/undoReserve");
        try {
            Form form = new Form();
            //传递用户名参数是为了能够创建用户预订记录的接口
            form.add("userName", userName);
            form.add("parkUid", parkUid);
            System.out.println("\n\n\n....................................................undoReserve hahahahahahhaha\n\n\n");
            Representation representation = form.getWebRepresentation();
            String text = representation.getText();
            StringRepresentation stringRepresentation = new StringRepresentation(text, MediaType.ALL);
            System.out.println("User undoReserve " + text);
            isSuccess = client.put(stringRepresentation).getText();

            System.out.println("\n\n\nundoReserve hahahahahahhaha....................................................\n\n\n");

            System.out.println("User undoReserve " + "{" + isSuccess + "}");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSuccess;
    }
}
