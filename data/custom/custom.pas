{
program Custom(dummy);
var i, max: integer;
    tmp: boolean;

procedure printResult(result: integer; withPrefix: boolean);
begin
    if(withPrefix)then
    begin
        writeln('Result: ', result)
    end
    else
    begin
        writeln(result)
    end
end;

procedure isPrime(n: integer);
var i: integer;
    p: boolean;
begin
    if(n <= 1)then
    begin
        tmp := false
    end
    else
    begin
        i := 2;
        p := true;
        while(i < n)do
        begin
            if(0 = (n mod i))then
            begin
                p := false
            end;
            i := i + 1
        end;
        tmp := p
    end
end;

begin
    i := 2;
    max := 0;
    while(i <= 1000)do
    begin
        isPrime(i);
        if(tmp)then
        begin
            printResult(i, false);
            max := i
        end;
        i := i + 1
    end;
    printResult(max, true)
end.
}

program test(dummy);
var a: integer;
    c: array [1..5] of char;

begin
	readln;
	readln(c);
	writeln(c[1], c[4]);
	writeln(c, ', wow!!')
end.

