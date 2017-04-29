package com.example.realgodjj.parking_system.client;

import com.example.realgodjj.parking_system.simulation.User;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;

public class ChangePasswordClient {
    public static String changePassword(String url, User user) {
        String isSuccess = "";
        ClientResource client = new ClientResource(url + "/changePassword");
        try {
            Form form = new Form();
            form.add("userId", String.valueOf(user.getUserId()));
            form.add("password", user.getPassword());
            System.out.println("\n\n\n....................................................change password hahahahahahhaha\n\n\n");
            Representation representation = form.getWebRepresentation();
            String text = representation.getText();
            StringRepresentation stringRepresentation = new StringRepresentation(text, MediaType.ALL);
            System.out.println("User change password " + text);
            isSuccess = client.put(stringRepresentation).getText();

            System.out.println("\n\n\nchange password hahahahahahhaha....................................................\n\n\n");

            System.out.println("User change password " + "{" + isSuccess + "}");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSuccess;
    }
}
