package com.transloadit.examples;

import com.transloadit.sdk.Assembly;
import com.transloadit.sdk.Transloadit;
import com.transloadit.sdk.exceptions.LocalOperationException;
import com.transloadit.sdk.exceptions.RequestException;
import com.transloadit.sdk.response.AssemblyResponse;

import java.io.File;

/**
 * This Example makes use of a Template to run Assembly instructions on a file.
 * This is done by passing the template id through the "template_id" parameter.
 */
public class TemplateExample {
    public static void main (String [] args) {
        Transloadit transloadit = new Transloadit("TRANSLOADIT_KEY", "TRANSLOADIT_SECRET");

        Assembly assembly = transloadit.newAssembly();
        // set template id
        assembly.addOption("template_id", "YOUR_TEMPLATE_ID");

        assembly.addFile(new File(ImageResizer.class.getResource("/lol_cat.jpg").getFile()));

        try {
            System.out.println("Uploading ...");
            AssemblyResponse response = assembly.save();

            // wait for assembly to finish executing.
            System.out.println("waiting for assembly to finish ...");
            while (!response.isFinished()) {
                response = transloadit.getAssemblyByUrl(response.getSslUrl());
            }

            String resultUrl = response.getStepResult("resize").getJSONObject(0).getString("url");
            System.out.println("Here's your assembly result: " + resultUrl);

        } catch (RequestException | LocalOperationException e) {
            e.printStackTrace();
        }
    }
}
