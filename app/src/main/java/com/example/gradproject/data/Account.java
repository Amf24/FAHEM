package com.example.gradproject.data;

public class Account
{
    private String accountId;
    private String fullName;
    private String email;
    private String phoneNumber;

    public Account()
    {

    }

    public Account(String accountId, String fullName, String email, String phoneNumber)
    {
        this.accountId = accountId;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public String getAccountId()
    {
        return accountId;
    }

    public String getFullName()
    {
        return fullName;
    }

    public String getEmail()
    {
        return email;
    }

    public String getPhoneNumber()
    {
        return phoneNumber;
    }
}
