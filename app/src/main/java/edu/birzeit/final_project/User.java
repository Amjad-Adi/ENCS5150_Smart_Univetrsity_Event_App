package edu.birzeit.final_project;


public class User {
    private String mEmail;
    private String mFirstName;
    private String mLastName;
    private String mPassword;   // stores the hashed password
    private String mGender;
    private String mCategory;
    private String mPhone;

    public User() {
    }

    public User(String mEmail, String mFirstName, String mLastName, String mPassword,
                String mGender, String mCategory, String mPhone) {
        this.mEmail = mEmail;
        this.mFirstName = mFirstName;
        this.mLastName = mLastName;
        this.mPassword = mPassword;
        this.mGender = mGender;
        this.mCategory = mCategory;
        this.mPhone = mPhone;
    }

    public String getmEmail() { return mEmail; }
    public void setmEmail(String mEmail) { this.mEmail = mEmail; }

    public String getmFirstName() { return mFirstName; }
    public void setmFirstName(String mFirstName) { this.mFirstName = mFirstName; }

    public String getmLastName() { return mLastName; }
    public void setmLastName(String mLastName) { this.mLastName = mLastName; }

    public String getmPassword() { return mPassword; }
    public void setmPassword(String mPassword) { this.mPassword = mPassword; }

    public String getmGender() { return mGender; }
    public void setmGender(String mGender) { this.mGender = mGender; }

    public String getmCategory() { return mCategory; }
    public void setmCategory(String mCategory) { this.mCategory = mCategory; }

    public String getmPhone() { return mPhone; }
    public void setmPhone(String mPhone) { this.mPhone = mPhone; }

    @Override
    public String toString() {
        return "User{" +
                "\nmEmail='" + mEmail + '\'' +
                "\n, mFirstName='" + mFirstName + '\'' +
                "\n, mLastName='" + mLastName + '\'' +
                "\n, mGender='" + mGender + '\'' +
                "\n, mCategory='" + mCategory + '\'' +
                "\n, mPhone='" + mPhone + '\'' +
                "\n}";
    }
}