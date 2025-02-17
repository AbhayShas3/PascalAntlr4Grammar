program BankAccount;

class Account
    private
        balance: integer;
        accountNumber: string;
    
    public
        constructor create;
        function getBalance: integer;
        procedure deposit;
        procedure withdraw;
end;

{ Constructor implementation }
constructor create.
begin
    accountNumber := '1234-5678';
    balance := 1000
end;

{ Method implementations }
function getBalance: integer;
begin
    getBalance := balance
end;

procedure deposit;
begin
    balance := balance + 500
end;

procedure withdraw;
begin
    if balance >= 200 then
        balance := balance - 200
end;

var
    myAccount: Account;
begin
    myAccount := Account.create;
    writeln(myAccount.getBalance);
    myAccount.deposit;
    writeln(myAccount.getBalance);
    myAccount.withdraw;
    writeln(myAccount.getBalance)
end.