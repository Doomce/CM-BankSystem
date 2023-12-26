
# BANK SYSTEM (*for CM*)
> [!IMPORTANT]
> Kodo kopijuoti nepatartina, nes naudojami pasenę (jau deprecated) sprendimai ir ***buvo programuojama taip, kad kažkas VEIKTŲ***. Taip pat, kodas kai kuriose vietose gali būti neatitinkantis JAVA programavimo standartų (*vis gi, tai pirmasis JAVA projektas*).
### Kaip atsirado ši idėja?
2018 - 2020m. laikotarpiu, ***Centrimiestis Network*** komandos iniciatyva buvo vystomas šio projekto Minecraft serveris. Prisiėmus įskiepių tvarkytojo rolę tekdavo ieškoti rinkoje jau egzistuojančių sprendimų (konkrečiau - plugin'ų). Tačiau, jau esantys įskiepiai neatitiko mūsų reikalavimų banko sistemai. Tuo tarpu mūsų projekto valdybai buvo pateikti du pasirinkimai: samdyti programuotoją arba bandyti kurti savo jėgomis.

### Pirmasis didesnės apimties projektas...
Turėdamas minimalius programavimo įgūdžius (***C++*** *(pradžia mokykloje)*, ***skript*** *(minecraft)*) priėmiau sprendimą apsiimti banko sistemos kūrimo darbais. Šiai sistemai iškelti reikalavimai buvo gan neįprasti tuomet rinkoje buvusiems Minecraft serveriams, nes ir serverio tipas buvo CityRP. Tad, projekto pradžia buvo gan sunkoka... Išbandžius programavimą su JAVA kalba, į projektą jau norėjosi įtraukti kuo daugiau integracijų, kaip Hibernate, Cache ir kitas minecraft įskiepių sąsajas.

***> Priminimas: Tuomet dar neegzistavo ChatGPT!***

### Šiomis dienomis...

```
Pasaulinė statistika rodo, kad kone 90 proc. startuolių žlunga nė nespėję iš esmės realizuoti savo verslo idėjos.
```
Projekto veikla buvo sustabdyta, tad ir idėjos plėtojimas atitinkamai buvo nutrauktas. Šiuo metu nėra planų šio kodo panaudojimui. Iš tiesų, norint prikelti šį projektą, galimai reikėtų perrašyti ne mažą dalį kodo.

Metant pesimizmą į šoną, nusprendžiau visą banko sistemos kodą įkelti į Github ir taip suteikti galimybę kitiems žmonėms panaudoti šį projektą, kaip idėjų šaltinį. Kodo kopijuoti nepatartina, nes naudojami pasenę (jau deprecated) sprendimai ir ***buvo programuojama taip, kad kažkas VEIKTŲ***. Taip pat, kodas kai kuriose vietose gali būti neatitinkantis JAVA programavimo standartų (*vis gi, tai pirmasis JAVA projektas*).


## Funkcionalumas
 + Banko sąskaitos sukūrimas. Prisimenant, yra nustatyti limitai bankų sąskaitų kiekiui kiekvienam žaidėjui pagal permissions.<br>
![image](https://github.com/Doomce/CM-BankSystem/assets/40797035/473fa7a3-6abd-497d-88fd-35e9afc462ff)<br>
 + Pagrindinis langas, kuriame žaidėjas gali valdyti savo banko sąskaitą.<br>
![Photo1](https://github.com/Doomce/CM-BankSystem/assets/40797035/9e73ab04-11a0-4dc4-ad36-ce1feafe273c)<br>
 + Pinigų įnešimo operacija (Silver ir Basic planams mokesčiai skiriasi):<br>
![Photo5](https://github.com/Doomce/CM-BankSystem/assets/40797035/e911b385-8ddc-4ffa-8c92-ef6bbc551695)<br>
 + Indėlių funkcija:<br>
![image](https://github.com/Doomce/CM-BankSystem/assets/40797035/001cb672-5505-4e70-bd9a-f4a442ca83f2)<br>
 + Galimybė išsiimti banko kortelę arba ją užblokuoti.<br>
![image](https://github.com/Doomce/CM-BankSystem/assets/40797035/edda6dc4-ac1a-4b6b-bd66-36e052be5cb1)<br>
 + Bankomato langas. Žalia - įnešimas. Geltona - balanso tikrinimas. Raudona - išgryninimas.<br>
![image](https://github.com/Doomce/CM-BankSystem/assets/40797035/ccc43afe-4e5c-4c32-8893-dca934ae92cc)<br>
 + Bankomatų valdymas. Banko administratorius gali pridėti arba išimti bankomatus. Taip pat juos įjungti arba išjungti.<br>
![image](https://github.com/Doomce/CM-BankSystem/assets/40797035/496d8785-8b14-49f2-ad07-1b8d16758589)<br>
 + Biudžetų valdymas. Kiekviena viešoji CM įstaiga turi savo biudžetą. Jo valdymas yra apribojamas. Tik turint permission'ą galima atlikti operacijas. Yra API, skirta biudžetų sąsajoms su kitais įskiepiais. Šiuo atveju, pateikta nuotrauka iš banko fondo. Kiekvienas žaidėjo sumokėtas mokestis keliauja į šio biudžeto sąskaitą.<br>
![image](https://github.com/Doomce/CM-BankSystem/assets/40797035/bdb430fa-9929-411d-b81d-f954720814d4)<br>
