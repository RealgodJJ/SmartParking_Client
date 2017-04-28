package com.example.realgodjj.parking_system.client;


import com.example.realgodjj.parking_system.simulation.User;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;

public class UpdateUserClient {
    public static String updateUser(String url, User user) {
        String isSuccess = "";
        ClientResource client = new ClientResource(url + "/updateUser");
        try {
            Form form = new Form();
            form.add("userId", String.valueOf(user.getUserId()));
            form.add("userName", user.getUserName());
            form.add("phoneNumber", user.getPhoneNumber());
            form.add("email", user.getEmail());
            form.add("plateNo", user.getPlateNo());
            System.out.println("\n\n\n....................................................update hahahahahahhaha\n\n\n");
            Representation representation = form.getWebRepresentation();
            String text = representation.getText();
            StringRepresentation stringRepresentation = new StringRepresentation(text, MediaType.ALL);
            System.out.println("User update " + text);
            isSuccess = client.put(stringRepresentation).getText();

            System.out.println("\n\n\nupdate hahahahahahhaha....................................................\n\n\n");

            System.out.println("User update " + "{" + isSuccess + "}");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSuccess;
    }
}
