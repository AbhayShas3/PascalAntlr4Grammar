program BankAccount;

class Account
    private
        balance: integer;
        accountNumber: string;
    
    public
        constructor create(number: string; initialBalance: integer);
        destructor destroy;
        
        function getBalance: integer;
        procedure deposit(amount: integer);
        procedure withdraw(amount: integer);
end;

constructor Account.create(number: string; initialBalance: integer);
begin
    accountNumber := number;
    balance := initialBalance;
end;

destructor Account.destroy;
begin
    // Cleanup code would go here
end;

function Account.getBalance: integer;
begin
    getBalance := balance;
end;

procedure Account.deposit(amount: integer);
begin
    balance := balance + amount;
end;

procedure Account.withdraw(amount: integer);
begin
    if amount <= balance then
        balance := balance - amount;
end;

var
    myAccount: Account;
begin
    myAccount := Account.create('1234-5678', 1000);
    
    writeln('Initial balance: ', myAccount.getBalance);
    myAccount.deposit(500);
    writeln('After deposit: ', myAccount.getBalance);
    myAccount.withdraw(200);
    writeln('After withdrawal: ', myAccount.getBalance);
    
    myAccount.destroy;
end.