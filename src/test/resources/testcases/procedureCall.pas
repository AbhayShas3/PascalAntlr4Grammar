program procedureCall;
var
    x : integer;
procedure myProc;
begin
    x := 10;
end;
begin
    x := 5;
    myProc;
end.
