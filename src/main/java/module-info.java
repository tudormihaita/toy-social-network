module application.toysocialnetwork {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;

    opens application.toysocialnetwork to javafx.fxml;
    exports application.toysocialnetwork;
    exports application.toysocialnetwork.controller;
    opens application.toysocialnetwork.controller to javafx.fxml;
    opens application.toysocialnetwork.domain to javafx.base;

    exports application.toysocialnetwork.service;
    exports application.toysocialnetwork.repository;
    exports application.toysocialnetwork.utils.events;
    exports application.toysocialnetwork.exceptions;
    exports application.toysocialnetwork.utils.observer;
    exports application.toysocialnetwork.domain;
    exports application.toysocialnetwork.validators;
    exports application.toysocialnetwork.utils;
    exports application.toysocialnetwork.domain.dto;
    opens application.toysocialnetwork.domain.dto to javafx.base;
    exports application.toysocialnetwork.domain.message;
    opens application.toysocialnetwork.domain.message to javafx.base;

}