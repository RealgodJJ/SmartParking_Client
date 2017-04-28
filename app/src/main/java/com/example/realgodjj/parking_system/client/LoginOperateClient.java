package com.example.realgodjj.parking_system.client;

import com.example.realgodjj.parking_system.simulation.User;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;

//用户登录客户端
public class LoginOperateClient {
    public static String login(String url, User user) {
        String isSuccess = "";
        ClientResource client = new ClientResource(url + "/login");
        try {
            Form form = new Form();
            form.add("userName",user.getUserName());
            form.add("password", user.getPassword());
            System.out.println("\n\n\n....................................................login hahahahahahhaha\n\n\n");
            Representation representation = form.getWebRepresentation();
            String text = representation.getText();
            StringRepresentation stringRepresentation = new StringRepresentation(text, MediaType.ALL);
            System.out.println("User login " + text);
            isSuccess = client.post(stringRepresentation).getText();

            System.out.println("\n\n\nlogin hahahahahahhaha....................................................\n\n\n");

            System.out.println("User login " + "{" + isSuccess + "}");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSuccess;
    }
}
