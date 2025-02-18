PROGRAM TestInheritance;

CLASS Animal
PUBLIC
  constructor Create;
  procedure Destroy;
  name: STRING;
END;

CLASS Dog INHERITS Animal
PRIVATE
  breed: STRING;
PUBLIC
  constructor Create;
  procedure Destroy;
END;

constructor Animal.Create;
BEGIN
  name := 'Generic Animal';
END;

constructor Dog.Create;
BEGIN
  breed := 'Unknown';
  name := 'Generic Dog';
END;

BEGIN
END.