package com.example.realgodjj.parking_system.client;


import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;

public class ParkInfoClient {
    public static String getByParkId(String url, int parkId) {
        String isSuccess = "";
        ClientResource client = new ClientResource(url + "/park/" + parkId + "?choose=id");
        try {
            Form form = new Form();
            form.add("userId", String.valueOf(parkId));
            System.out.println("\n\n\n....................................................getByParkId hahahahahahhaha\n\n\n");
            Representation representation = form.getWebRepresentation();
            String text = representation.getText();
            StringRepresentation stringRepresentation = new StringRepresentation(text, MediaType.ALL);
            System.out.println("User getByParkId " + text);
            isSuccess = client.post(stringRepresentation).getText();

            System.out.println("\n\n\ngetByParkId hahahahahahhaha....................................................\n\n\n");

            System.out.println("User getByParkId " + "{" + isSuccess + "}");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

    public static String getByParkUid(String url, String parkUid) {
        String isSuccess = "";
        ClientResource client = new ClientResource(url + "/park/" + parkUid + "?choose=parkUid");
        try {
            Form form = new Form();
            form.add("parkUid", parkUid);
            System.out.println("\n\n\n....................................................getByParkUid hahahahahahhaha\n\n\n");
            Representation representation = form.getWebRepresentation();
            String text = representation.getText();
            StringRepresentation stringRepresentation = new StringRepresentation(text, MediaType.ALL);
            System.out.println("User getByParkUid " + text);

            //isSuccess = client.post(stringRepresentation).getText();
            isSuccess = client.get().getText();

            System.out.println("\n\n\ngetByParkUid hahahahahahhaha....................................................\n\n\n");

            System.out.println("User getByParkUid " + "{" + isSuccess + "}");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSuccess;
    }
}
