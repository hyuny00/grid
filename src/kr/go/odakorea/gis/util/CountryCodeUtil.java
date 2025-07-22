package kr.go.odakorea.gis.util;

import java.util.HashMap;
import java.util.Map;

public class CountryCodeUtil {

    private static final Map<String, String> alpha2ToAlpha3 = new HashMap<>();
    private static final Map<String, String> alpha3ToAlpha2 = new HashMap<>();
    private static final Map<String, String> nameToAlpha2 = new HashMap<>();
    private static final Map<String, String> nameToAlpha3 = new HashMap<>();

    static {
        addMapping("AW", "ABW", "Aruba");
        addMapping("AF", "AFG", "Afghanistan");
        addMapping("AO", "AGO", "Angola");
        addMapping("AI", "AIA", "Anguilla");
        addMapping("AX", "ALA", "Åland Islands");
        addMapping("AL", "ALB", "Albania");
        addMapping("AD", "AND", "Andorra");
        addMapping("AE", "ARE", "United Arab Emirates");
        addMapping("AR", "ARG", "Argentina");
        addMapping("AM", "ARM", "Armenia");
        addMapping("AS", "ASM", "American Samoa");
        addMapping("AQ", "ATA", "Antarctica");
        addMapping("TF", "ATF", "French Southern Territories");
        addMapping("AG", "ATG", "Antigua and Barbuda");
        addMapping("AU", "AUS", "Australia");
        addMapping("AT", "AUT", "Austria");
        addMapping("AZ", "AZE", "Azerbaijan");
        addMapping("BI", "BDI", "Burundi");
        addMapping("BE", "BEL", "Belgium");
        addMapping("BJ", "BEN", "Benin");
        addMapping("BQ", "BES", "Bonaire, Sint Eustatius and Saba");
        addMapping("BF", "BFA", "Burkina Faso");
        addMapping("BD", "BGD", "Bangladesh");
        addMapping("BG", "BGR", "Bulgaria");
        addMapping("BH", "BHR", "Bahrain");
        addMapping("BS", "BHS", "Bahamas");
        addMapping("BA", "BIH", "Bosnia and Herzegovina");
        addMapping("BL", "BLM", "Saint Barthélemy");
        addMapping("BY", "BLR", "Belarus");
        addMapping("BZ", "BLZ", "Belize");
        addMapping("BM", "BMU", "Bermuda");
        addMapping("BO", "BOL", "Bolivia, Plurinational State of");
        addMapping("BR", "BRA", "Brazil");
        addMapping("BB", "BRB", "Barbados");
        addMapping("BN", "BRN", "Brunei Darussalam");
        addMapping("BT", "BTN", "Bhutan");
        addMapping("BV", "BVT", "Bouvet Island");
        addMapping("BW", "BWA", "Botswana");
        addMapping("CF", "CAF", "Central African Republic");
        addMapping("CA", "CAN", "Canada");
        addMapping("CC", "CCK", "Cocos (Keeling) Islands");
        addMapping("CH", "CHE", "Switzerland");
        addMapping("CL", "CHL", "Chile");
        addMapping("CN", "CHN", "China");
        addMapping("CI", "CIV", "Côte d'Ivoire");
        addMapping("CM", "CMR", "Cameroon");
        addMapping("CD", "COD", "Congo, The Democratic Republic of the");
        addMapping("CG", "COG", "Congo");
        addMapping("CK", "COK", "Cook Islands");
        addMapping("CO", "COL", "Colombia");
        addMapping("KM", "COM", "Comoros");
        addMapping("CV", "CPV", "Cabo Verde");
        addMapping("CR", "CRI", "Costa Rica");
        addMapping("CU", "CUB", "Cuba");
        addMapping("CW", "CUW", "Curaçao");
        addMapping("CX", "CXR", "Christmas Island");
        addMapping("KY", "CYM", "Cayman Islands");
        addMapping("CY", "CYP", "Cyprus");
        addMapping("CZ", "CZE", "Czechia");
        addMapping("DE", "DEU", "Germany");
        addMapping("DJ", "DJI", "Djibouti");
        addMapping("DM", "DMA", "Dominica");
        addMapping("DK", "DNK", "Denmark");
        addMapping("DO", "DOM", "Dominican Republic");
        addMapping("DZ", "DZA", "Algeria");
        addMapping("EC", "ECU", "Ecuador");
        addMapping("EG", "EGY", "Egypt");
        addMapping("ER", "ERI", "Eritrea");
        addMapping("EH", "ESH", "Western Sahara");
        addMapping("ES", "ESP", "Spain");
        addMapping("EE", "EST", "Estonia");
        addMapping("ET", "ETH", "Ethiopia");
        addMapping("FI", "FIN", "Finland");
        addMapping("FJ", "FJI", "Fiji");
        addMapping("FK", "FLK", "Falkland Islands (Malvinas)");
        addMapping("FR", "FRA", "France");
        addMapping("FO", "FRO", "Faroe Islands");
        addMapping("FM", "FSM", "Micronesia, Federated States of");
        addMapping("GA", "GAB", "Gabon");
        addMapping("GB", "GBR", "United Kingdom");
        addMapping("GE", "GEO", "Georgia");
        addMapping("GG", "GGY", "Guernsey");
        addMapping("GH", "GHA", "Ghana");
        addMapping("GI", "GIB", "Gibraltar");
        addMapping("GN", "GIN", "Guinea");
        addMapping("GP", "GLP", "Guadeloupe");
        addMapping("GM", "GMB", "Gambia");
        addMapping("GW", "GNB", "Guinea-Bissau");
        addMapping("GQ", "GNQ", "Equatorial Guinea");
        addMapping("GR", "GRC", "Greece");
        addMapping("GD", "GRD", "Grenada");
        addMapping("GL", "GRL", "Greenland");
        addMapping("GT", "GTM", "Guatemala");
        addMapping("GF", "GUF", "French Guiana");
        addMapping("GU", "GUM", "Guam");
        addMapping("GY", "GUY", "Guyana");
        addMapping("HK", "HKG", "Hong Kong");
        addMapping("HM", "HMD", "Heard Island and McDonald Islands");
        addMapping("HN", "HND", "Honduras");
        addMapping("HR", "HRV", "Croatia");
        addMapping("HT", "HTI", "Haiti");
        addMapping("HU", "HUN", "Hungary");
        addMapping("ID", "IDN", "Indonesia");
        addMapping("IM", "IMN", "Isle of Man");
        addMapping("IN", "IND", "India");
        addMapping("IO", "IOT", "British Indian Ocean Territory");
        addMapping("IE", "IRL", "Ireland");
        addMapping("IR", "IRN", "Iran, Islamic Republic of");
        addMapping("IQ", "IRQ", "Iraq");
        addMapping("IS", "ISL", "Iceland");
        addMapping("IL", "ISR", "Israel");
        addMapping("IT", "ITA", "Italy");
        addMapping("JM", "JAM", "Jamaica");
        addMapping("JE", "JEY", "Jersey");
        addMapping("JO", "JOR", "Jordan");
        addMapping("JP", "JPN", "Japan");
        addMapping("KZ", "KAZ", "Kazakhstan");
        addMapping("KE", "KEN", "Kenya");
        addMapping("KG", "KGZ", "Kyrgyzstan");
        addMapping("KH", "KHM", "Cambodia");
        addMapping("KI", "KIR", "Kiribati");
        addMapping("KN", "KNA", "Saint Kitts and Nevis");
        addMapping("KR", "KOR", "Korea, Republic of");
        addMapping("KW", "KWT", "Kuwait");
        addMapping("LA", "LAO", "Lao People's Democratic Republic");
        addMapping("LB", "LBN", "Lebanon");
        addMapping("LR", "LBR", "Liberia");
        addMapping("LY", "LBY", "Libya");
        addMapping("LC", "LCA", "Saint Lucia");
        addMapping("LI", "LIE", "Liechtenstein");
        addMapping("LK", "LKA", "Sri Lanka");
        addMapping("LS", "LSO", "Lesotho");
        addMapping("LT", "LTU", "Lithuania");
        addMapping("LU", "LUX", "Luxembourg");
        addMapping("LV", "LVA", "Latvia");
        addMapping("MO", "MAC", "Macao");
        addMapping("MF", "MAF", "Saint Martin (French part)");
        addMapping("MA", "MAR", "Morocco");
        addMapping("MC", "MCO", "Monaco");
        addMapping("MD", "MDA", "Moldova, Republic of");
        addMapping("MG", "MDG", "Madagascar");
        addMapping("MV", "MDV", "Maldives");
        addMapping("MX", "MEX", "Mexico");
        addMapping("MH", "MHL", "Marshall Islands");
        addMapping("MK", "MKD", "North Macedonia");
        addMapping("ML", "MLI", "Mali");
        addMapping("MT", "MLT", "Malta");
        addMapping("MM", "MMR", "Myanmar");
        addMapping("ME", "MNE", "Montenegro");
        addMapping("MN", "MNG", "Mongolia");
        addMapping("MP", "MNP", "Northern Mariana Islands");
        addMapping("MZ", "MOZ", "Mozambique");
        addMapping("MR", "MRT", "Mauritania");
        addMapping("MS", "MSR", "Montserrat");
        addMapping("MQ", "MTQ", "Martinique");
        addMapping("MU", "MUS", "Mauritius");
        addMapping("MW", "MWI", "Malawi");
        addMapping("MY", "MYS", "Malaysia");
        addMapping("YT", "MYT", "Mayotte");
        addMapping("NA", "NAM", "Namibia");
        addMapping("NC", "NCL", "New Caledonia");
        addMapping("NE", "NER", "Niger");
        addMapping("NF", "NFK", "Norfolk Island");
        addMapping("NG", "NGA", "Nigeria");
        addMapping("NI", "NIC", "Nicaragua");
        addMapping("NU", "NIU", "Niue");
        addMapping("NL", "NLD", "Netherlands");
        addMapping("NO", "NOR", "Norway");
        addMapping("NP", "NPL", "Nepal");
        addMapping("NR", "NRU", "Nauru");
        addMapping("NZ", "NZL", "New Zealand");
        addMapping("OM", "OMN", "Oman");
        addMapping("PK", "PAK", "Pakistan");
        addMapping("PA", "PAN", "Panama");
        addMapping("PN", "PCN", "Pitcairn");
        addMapping("PE", "PER", "Peru");
        addMapping("PH", "PHL", "Philippines");
        addMapping("PW", "PLW", "Palau");
        addMapping("PG", "PNG", "Papua New Guinea");
        addMapping("PL", "POL", "Poland");
        addMapping("PR", "PRI", "Puerto Rico");
        addMapping("KP", "PRK", "Korea, Democratic People's Republic of");
        addMapping("PT", "PRT", "Portugal");
        addMapping("PY", "PRY", "Paraguay");
        addMapping("PS", "PSE", "Palestine, State of");
        addMapping("PF", "PYF", "French Polynesia");
        addMapping("QA", "QAT", "Qatar");
        addMapping("RE", "REU", "Réunion");
        addMapping("RO", "ROU", "Romania");
        addMapping("RU", "RUS", "Russian Federation");
        addMapping("RW", "RWA", "Rwanda");
        addMapping("SA", "SAU", "Saudi Arabia");
        addMapping("SD", "SDN", "Sudan");
        addMapping("SN", "SEN", "Senegal");
        addMapping("SG", "SGP", "Singapore");
        addMapping("GS", "SGS", "South Georgia and the South Sandwich Islands");
        addMapping("SH", "SHN", "Saint Helena, Ascension and Tristan da Cunha");
        addMapping("SJ", "SJM", "Svalbard and Jan Mayen");
        addMapping("SB", "SLB", "Solomon Islands");
        addMapping("SL", "SLE", "Sierra Leone");
        addMapping("SV", "SLV", "El Salvador");
        addMapping("SM", "SMR", "San Marino");
        addMapping("SO", "SOM", "Somalia");
        addMapping("PM", "SPM", "Saint Pierre and Miquelon");
        addMapping("RS", "SRB", "Serbia");
        addMapping("SS", "SSD", "South Sudan");
        addMapping("ST", "STP", "Sao Tome and Principe");
        addMapping("SR", "SUR", "Suriname");
        addMapping("SK", "SVK", "Slovakia");
        addMapping("SI", "SVN", "Slovenia");
        addMapping("SE", "SWE", "Sweden");
        addMapping("SZ", "SWZ", "Eswatini");
        addMapping("SX", "SXM", "Sint Maarten (Dutch part)");
        addMapping("SC", "SYC", "Seychelles");
        addMapping("SY", "SYR", "Syrian Arab Republic");
        addMapping("TC", "TCA", "Turks and Caicos Islands");
        addMapping("TD", "TCD", "Chad");
        addMapping("TG", "TGO", "Togo");
        addMapping("TH", "THA", "Thailand");
        addMapping("TJ", "TJK", "Tajikistan");
        addMapping("TK", "TKL", "Tokelau");
        addMapping("TM", "TKM", "Turkmenistan");
        addMapping("TL", "TLS", "Timor-Leste");
        addMapping("TO", "TON", "Tonga");
        addMapping("TT", "TTO", "Trinidad and Tobago");
        addMapping("TN", "TUN", "Tunisia");
        addMapping("TR", "TUR", "Turkey");
        addMapping("TV", "TUV", "Tuvalu");
        addMapping("TW", "TWN", "Taiwan, Province of China");
        addMapping("TZ", "TZA", "Tanzania, United Republic of");
        addMapping("UG", "UGA", "Uganda");
        addMapping("UA", "UKR", "Ukraine");
        addMapping("UM", "UMI", "United States Minor Outlying Islands");
        addMapping("UY", "URY", "Uruguay");
        addMapping("US", "USA", "United States");
        addMapping("UZ", "UZB", "Uzbekistan");
        addMapping("VA", "VAT", "Holy See (Vatican City State)");
        addMapping("VC", "VCT", "Saint Vincent and the Grenadines");
        addMapping("VE", "VEN", "Venezuela, Bolivarian Republic of");
        addMapping("VG", "VGB", "Virgin Islands, British");
        addMapping("VI", "VIR", "Virgin Islands, U.S.");
        addMapping("VN", "VNM", "Viet Nam");
        addMapping("VU", "VUT", "Vanuatu");
        addMapping("WF", "WLF", "Wallis and Futuna");
        addMapping("WS", "WSM", "Samoa");
        addMapping("YE", "YEM", "Yemen");
        addMapping("ZA", "ZAF", "South Africa");
        addMapping("ZM", "ZMB", "Zambia");
        addMapping("ZW", "ZWE", "Zimbabwe");
    }

    private static void addMapping(String alpha2, String alpha3, String countryName) {
        alpha2ToAlpha3.put(alpha2.toUpperCase(), alpha3.toUpperCase());
        alpha3ToAlpha2.put(alpha3.toUpperCase(), alpha2.toUpperCase());
        nameToAlpha2.put(countryName.toUpperCase(), alpha2.toUpperCase());
        nameToAlpha3.put(countryName.toUpperCase(), alpha3.toUpperCase());
    }

    /**
     * Alpha-2 코드를 Alpha-3 코드로 변환
     */
    public static String toAlpha3(String alpha2Code) {
        return alpha2ToAlpha3.get(alpha2Code.toUpperCase());
    }

    /**
     * Alpha-3 코드를 Alpha-2 코드로 변환
     */
    public static String toAlpha2(String alpha3Code) {
        return alpha3ToAlpha2.get(alpha3Code.toUpperCase());
    }

    /**
     * 국가명을 Alpha-2 코드로 변환
     */
    public static String getAlpha2ByName(String countryName) {
        return nameToAlpha2.get(countryName.toUpperCase());
    }

    /**
     * 국가명을 Alpha-3 코드로 변환
     */
    public static String getAlpha3ByName(String countryName) {
        return nameToAlpha3.get(countryName.toUpperCase());
    }

    /**
     * 테스트용 메인 메소드
     */
    public static void main(String[] args) {
        // 테스트 예제
        System.out.println("KR -> " + toAlpha3("KR")); // KOR
        System.out.println("KOR -> " + toAlpha2("KOR")); // KR
        System.out.println("Korea, Republic of -> " + getAlpha2ByName("Korea, Republic of")); // KR
        System.out.println("Korea, Republic of -> " + getAlpha3ByName("Korea, Republic of")); // KOR
        System.out.println("United States -> " + getAlpha2ByName("United States")); // US
        System.out.println("United States -> " + getAlpha3ByName("United States")); // USA
        System.out.println("Japan -> " + getAlpha2ByName("Japan")); // JP
        System.out.println("China -> " + getAlpha3ByName("China")); // CHN
    }
}