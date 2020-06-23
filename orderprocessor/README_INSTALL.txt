- Bygg -

maven clean package
	Zip arkiv skapas, extrahera på server

- Konfigurera -

chmod u+x order-processor.sh
	Så order-processor.sh kan köras
	Konfigurera vid behov om endpoints för in/ut/invalid

- Kör -

./order-processor.sh (start|stop|restart)

Lägg filer som ska processas i ./input

Processade giltiga ordrar hamnar i ./output

Icke giltiga hamnar i ./output/invalid

- Övrigt -

Om schemat ändras kan man lägga in ett nytt i ./schema med samma filnamn

- Förbättringar -

	Det finns ingen direkt notifieringsmekanism, men felaktiga ordrar landar i ./output/invalid
	Enhetstestet slutade fungera av någon anledning som jag inte riktigt haft tid att kolla tillräckligt länge på
	Versionsnumrering och releasehantering av projektet
	För riktig produktion behöver start/stop skriptet modifieras för chkconfig eller liknande
	