package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Contacts {
    private int contactId;
    private String contactName;
    private String contactEmail;

    private static ObservableList<Contacts> contacts = FXCollections.observableArrayList();

    public Contacts(int contactId, String contactName, String contactEmail){
        this.contactId = contactId;
        this.contactName = contactName;
        this.contactEmail = contactEmail;
    }

    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public static void addContacts(Contacts newContact) {
        contacts.add(newContact);
    }

    public int getContactId() {
        return contactId;
    }

    public String getContactName() {
        return contactName;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public static ObservableList<Contacts> getContacts(){
        return contacts;
    }

    @Override
    public String toString(){
        return (contactName);
    }
}
