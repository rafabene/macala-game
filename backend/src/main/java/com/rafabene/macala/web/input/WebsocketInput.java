package com.rafabene.macala.web.input;

public class WebsocketInput {

    // Represents the Intention of the Game user
    private InputOperation operation;

    // To be used for any parameter for the action
    private String parameter;

    /** Empty constrcutor needed by the  Websocke t Encoder/Decoder */
    public WebsocketInput() {

    }

    public WebsocketInput(InputOperation operation, String parameter) {
        this.operation = operation;
        this.parameter = parameter;
    }

    public InputOperation getOperation() {
        return this.operation;
    }

    public void setOperation(InputOperation operation) {
        this.operation = operation;
    }

    public String getParameter() {
        return this.parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    @Override
    public String toString() {
        return "{" +
                " operation='" + getOperation() + "'" +
                ", parameter='" + getParameter() + "'" +
                "}";
    }

}
