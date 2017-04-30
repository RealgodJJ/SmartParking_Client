package com.example.realgodjj.parking_system.client;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;

public class UserInfoClient {
    public static String getByUserId(String url, int userId) {
        String isSuccess = "";
        ClientResource client = new ClientResource(url + "/user/" + userId + "?choose=userId");
        try {
            Form form = new Form();
            form.add("userId", String.valueOf(userId));
            System.out.println("\n\n\n....................................................getByUserId hahahahahahhaha\n\n\n");
            Representation representation = form.getWebRepresentation();
            String text = representation.getText();
            StringRepresentation stringRepresentation = new StringRepresentation(text, MediaType.ALL);
            System.out.println("User getByUserId " + text);
            isSuccess = client.post(stringRepresentation).getText();

            System.out.println("\n\n\ngetByUserId hahahahahahhaha....................................................\n\n\n");

            System.out.println("User getByUserId " + "{" + isSuccess + "}");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

    public static String getByUserName(String url, String userName) {
        String isSuccess = "";
        ClientResource client = new ClientResource(url + "/user/" + userName + "?choose=userName");
        try {
            Form form = new Form();
            form.add("userName", userName);
            System.out.println("\n\n\n....................................................getByUserName hahahahahahhaha\n\n\n");
            Representation representation = form.getWebRepresentation();
            String text = representation.getText();
            StringRepresentation stringRepresentation = new StringRepresentation(text, MediaType.ALL);
            System.out.println("User getByUserName " + text);

            //isSuccess = client.post(stringRepresentation).getText();
            isSuccess = client.get().getText();

            System.out.println("\n\n\ngetByUserName hahahahahahhaha....................................................\n\n\n");

            System.out.println("User getByUserName " + "{" + isSuccess + "}");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

    public static String getByPhoneNumber(String url, String phoneNumber) {
        String isSuccess = "";
        ClientResource client = new ClientResource(url + "/user/" + phoneNumber + "?choose=phoneNumber");
        try {
            Form form = new Form();
            form.add("phoneNumber", phoneNumber);
            System.out.println("\n\n\n....................................................getByPhoneNumber hahahahahahhaha\n\n\n");
            Representation representation = form.getWebRepresentation();
            String text = representation.getText();
            StringRepresentation stringRepresentation = new StringRepresentation(text, MediaType.ALL);
            System.out.println("User getByPhoneNumber " + text);

            isSuccess = client.get().getText();

            System.out.println("\n\n\ngetByPhoneNumber hahahahahahhaha....................................................\n\n\n");

            System.out.println("User getByPhoneNumber " + "{" + isSuccess + "}");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

    public static String getByEmail(String url, String email) {
        String isSuccess = "";
        ClientResource client = new ClientResource(url + "/user/" + email + "?choose=email");
        try {
            Form form = new Form();
            form.add("email", email);
            System.out.println("\n\n\n....................................................getByEmail hahahahahahhaha\n\n\n");
            Representation representation = form.getWebRepresentation();
            String text = representation.getText();
            StringRepresentation stringRepresentation = new StringRepresentation(text, MediaType.ALL);
            System.out.println("User getByEmail " + text);

            isSuccess = client.get().getText();

            System.out.println("\n\n\ngetByEmail hahahahahahhaha....................................................\n\n\n");

            System.out.println("User getByEmail " + "{" + isSuccess + "}");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

    public static String getByPlateNo(String url, String plateNo) {
        String isSuccess = "";
        ClientResource client = new ClientResource(url + "/user/" + plateNo + "?choose=plateNo");
        try {
            Form form = new Form();
            form.add("plateNo", plateNo);
            System.out.println("\n\n\n....................................................getByPlateNo hahahahahahhaha\n\n\n");
            Representation representation = form.getWebRepresentation();
            String text = representation.getText();
            StringRepresentation stringRepresentation = new StringRepresentation(text, MediaType.ALL);
            System.out.println("User getByPlateNo " + text);

            isSuccess = client.get().getText();

            System.out.println("\n\n\ngetByPlateNo hahahahahahhaha....................................................\n\n\n");

            System.out.println("User getByPlateNo " + "{" + isSuccess + "}");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSuccess;
    }
}
