package com.example.realgodjj.parking_system.client;

import com.example.realgodjj.parking_system.simulation.Park;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;

public class UpdateParkClient {
    public static String updatePark(String url, Park park) {
        String isSuccess = "";
        ClientResource client = new ClientResource(url + "/updatePark");
        try {
            Form form = new Form();
            form.add("parkUid", String.valueOf(park.getParkUid()));
            form.add("totalSpaces", String.valueOf(park.getTotalSpaces()));
            form.add("totalAvailable", String.valueOf(park.getTotalAvailable()));
            form.add("parkPrice", String.valueOf(park.getParkPrice()));
            form.add("parkNightPrice", String.valueOf(park.getParkNightPrice()));
            System.out.println("\n\n\n....................................................update hahahahahahhaha\n\n\n");
            Representation representation = form.getWebRepresentation();
            String text = representation.getText();
            StringRepresentation stringRepresentation = new StringRepresentation(text, MediaType.ALL);
            System.out.println("Park update " + text);
            isSuccess = client.put(stringRepresentation).getText();

            System.out.println("\n\n\nupdate hahahahahahhaha....................................................\n\n\n");

            System.out.println("Park update " + "{" + isSuccess + "}");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSuccess;
    }
}
