echo powershell -Command "& {}"

powershell -Command "& {start -WorkingDirectory .\aserron-dlocal-merchant\ mvn -ArgumentList "spring-boot:run"}"
powershell -Command "& {start -WorkingDirectory .\aserron-dlocal-pm\ mvn -ArgumentList "spring-boot:run"}"