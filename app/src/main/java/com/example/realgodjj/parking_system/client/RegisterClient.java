package com.example.realgodjj.parking_system.client;

import com.example.realgodjj.parking_system.simulation.User;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;

//用户注册客户端
public class RegisterClient {
    public static String Register(String url, User user) {
        String isSuccess = "";
        ClientResource client = new ClientResource(url + "/register");
        try {
            Form form = new Form();
            form.add("userName",user.getUserName());
            form.add("password", user.getPassword());
            form.add("phoneNumber", user.getPhoneNumber());
            form.add("email", user.getEmail());
            form.add("plateNo", user.getPlateNo());
            System.out.println("\n\n\n....................................................register hahahahahahhaha\n\n\n");
            Representation representation = form.getWebRepresentation();
            String text = representation.getText();
            StringRepresentation stringRepresentation = new StringRepresentation(text, MediaType.ALL);
            System.out.println("User register " + text);
            isSuccess = client.post(stringRepresentation).getText();

            System.out.println("\n\n\nregister hahahahahahhaha....................................................\n\n\n");

            System.out.println("User register " + "{" + isSuccess + "}");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSuccess;
    }
}
