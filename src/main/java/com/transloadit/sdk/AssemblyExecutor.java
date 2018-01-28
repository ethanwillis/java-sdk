package com.transloadit.sdk;


import com.transloadit.sdk.exceptions.LocalOperationException;
import com.transloadit.sdk.exceptions.RequestException;
import io.tus.java.client.ProtocolException;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AssemblyExecutor {
    private final ExecutorService service;
    private Assembly assembly;

    public AssemblyExecutor(Assembly assembly) {
        this.assembly =  assembly;
        service = Executors.newSingleThreadExecutor();
    }

    public void execute() {
        service.execute(new AssemblyRunnable());
    }

    void close() {
        // todo investigate how this functions
        service.shutdown();
    }

    private class AssemblyRunnable implements Runnable {
        @Override
        public void run() {
            try {
                assembly.uploadFiles();
                assembly.getListener().onUploadFinished();
            } catch (ProtocolException e) {
                assembly.getListener().onUploadFailed(e);
                close();
                return;
            } catch (IOException e) {
                assembly.getListener().onUploadFailed(e);
                close();
                return;
            }

            try {
                assembly.getListener().onAssemblyFinished(assembly.watchStatus());
            } catch (LocalOperationException e) {
                assembly.getListener().onAssemblyStatusUpdateFailed(e);
            } catch (RequestException e) {
                assembly.getListener().onAssemblyStatusUpdateFailed(e);
            } catch (InterruptedException e) {
                assembly.getListener().onAssemblyStatusUpdateFailed(e);
            } finally {
                close();
            }
        }
    }
}

