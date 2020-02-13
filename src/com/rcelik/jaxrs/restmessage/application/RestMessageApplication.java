package com.rcelik.jaxrs.restmessage.application;

import com.rcelik.jaxrs.restmessage.RestMessageController;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

public class RestMessageApplication extends Application {

    private Set<Object> singletons = new HashSet<>();


    public RestMessageApplication(){
        singletons.add(new RestMessageController());
    }

    // means these singletons are singleton, once they are created when they first loaded to servlet context.
    // and they never deleted by GC until jvm dies.
    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }

    // this is used when root resource is wanted to be request scope, means it is created every request come to that resouce.
    @Override
    public Set<Class<?>> getClasses() {
        return super.getClasses();
    }
}
