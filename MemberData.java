public class MemberData {
    private int memberID;
    private String name;
    private String phoneNum;
    private int acctBalance;
    private int moneySpent;
    private String membershipName;

    public int getAcctBalance() { return acctBalance; }

    public void setAcctBalance(int amnt) { this.acctBalance = amnt;}

    public int getMoneySpent() { return moneySpent; }

    public void setMoneySpent(int amnt) { this.moneySpent = amnt;}

    public String getMembershipName() { return membershipName; }

    public void setMembershipName(String n) { this.membershipName = n;}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public int getMemberID() {
        return memberID;
    }

    public void setMemberID(int memberID) {
        this.memberID = memberID;
    }

    public String insertString() {
        String retval = "(";
        retval += memberID + ", '" + name + "', '" + phoneNum + "', "
                + acctBalance + ", " + moneySpent + ", '" + membershipName + "')";
        return retval;
    }
}
