package org.gproman.report;

import org.gproman.GproManager;
import org.gproman.model.Manager;
import org.gproman.model.car.Car;
import org.gproman.model.car.CarPart;
import org.gproman.model.driver.Driver;
import org.gproman.model.driver.DriverAttributes;
import org.gproman.model.race.Race;
import org.gproman.model.race.TestSession;
import org.gproman.model.race.TestStint;
import org.gproman.model.season.Season;
import org.gproman.model.season.TyreSupplierAttrs;
import org.gproman.model.staff.Facilities;
import org.gproman.model.staff.TDAttributes;
import org.gproman.model.staff.TechDirector;

public class TestReportGenerator {

    public static class BBReportGenerator
            implements
            ReportGenerator {

        public String generate(Manager manager,
                               Season season,
                               Race race,
                               TyreSupplierAttrs supplier) {
            StringBuilder builder = new StringBuilder();
            TestSession ts = race.getTestSession();
            if ( ts != null ) {
                generateTitle( builder, race, manager );
                generateRaceWeather( builder, ts );
                generateTyreSupplier( season, builder );
                builder.append( "[/table]" );
                generateTestPoints( builder, ts );
                builder.append( "[table=2]" );
                generateDriver( builder, race );
                builder.append( "[c]" );
                generateTD( builder, race );
                builder.append( "[/table]" );

                generateStints( builder, ts );
                generateCar( builder, ts );
                generateFacilities( builder, race );
                
            }
            builder.append( "[right][i]Relatório de Testes gerado por GPRO Manager's Toolbox " + GproManager.getVersionString() + "[/i][/right]" );
            return builder.toString();
        }

        public void generateTitle(StringBuilder builder,
                                  Race race,
                                  Manager manager) {
            builder.append( String.format( "[table=8,S%02dR%02d - %s - correndo na %s,0]", race.getSeasonNumber(), race.getNumber(), race.getTestSession().getTrack().getName(), manager.getGroup() ) );
        }

        public void generateRaceWeather(StringBuilder builder,
                                        TestSession ts) {
            builder.append( String.format( "[big][b][center]Clima[/center][/b][/big][c][big][center]%s[/center][/big]"+
                                           "[c][big][b][center]Temperatura[/center][/b][/big][c][big][center]%d°C[/center][/big]"+
                                           "[c][big][b][center]Umidade[/center][/b][/big][c][big][center]%d%%[/center][/big]",
                                           ts.getWeather().bbCode,
                                           ts.getTemperature(),
                                           ts.getHumidity() ) );
        }

        private void generateTyreSupplier(Season season,
                                          StringBuilder builder) {
            builder.append( "[c][big][b][center]Fornecedor[/center][/b][/big][c][big]" + (season.getSupplier() != null ? season.getSupplier().toString() : "<não disponível>") + "[/big] " );
            builder.append( season.getSupplier() != null ? season.getSupplier().getBBCode() : "" );
        }
        
        public void generateStints(StringBuilder builder,
                                   TestSession ts) {
            if ( !ts.getStints().isEmpty() ) {
                builder.append( String.format( "[table=15,Stints %d/10 - Voltas %d/100,1]" +
                                               "[big]#[/big][c][big]Voltas[/big][c][big]Melhor[/big][c][big]Média[/big][c][big]FWg[/big][c][big]RWg[/big][c][big]Eng[/big][c][big]Bra[/big][c][big]Gea[/big][c][big]Sus[/big][c][big]Pneus[/big][c][big]Comb.[/big][c][big]Desg[/big][c][big]Comb. Final[/big][c][big]Prioridade[/big]\n",
                                               ts.getStintsDone(),
                                               ts.getLapsDone() ) );
                for ( TestStint stint : ts.getStints() ) {
                    builder.append( String.format( "[c][big]%2d[/big][c][big]%d/%d[/big][c][big]%8s[/big][c][big]%8s[/big][c][big]%3d[/big][c][big]%3d[/big][c][big]%3d[/big][c][big]%3d[/big][c][big]%3d[/big][c][big]%3d[/big][c][big]%-8s[/big][c][big]%3d lts[/big][c][big]%3d%%[/big][c][big]%4d lts[/big][c][big]%s[/big]\n",                                                   stint.getNumber(),
                                                   stint.getLapsDone(),
                                                   stint.getLapsPlanned(),
                                                   formatTime( stint.getBestTime() ),
                                                   formatTime( stint.getMeanTime() ),
                                                   stint.getSettings().getFrontWing(),
                                                   stint.getSettings().getRearWing(),
                                                   stint.getSettings().getEngine(),
                                                   stint.getSettings().getBrakes(),
                                                   stint.getSettings().getGear(),
                                                   stint.getSettings().getSuspension(),
                                                   stint.getSettings().getTyre(),
                                                   stint.getFuelStart(),
                                                   stint.getTyresEnd(),
                                                   stint.getFuelEnd(),
                                                   stint.getPriority().mnemPtBr ) );
                }
                builder.append( "[/table]" );
            } else {
                builder.append( String.format( "[table=15,Stints %d/10 - Voltas %d/100,1][/table]",
                        ts.getStintsDone(),
                        ts.getLapsDone() ) );
            }
        }

        public void generateCar(StringBuilder builder,
                                TestSession ts) {

            builder.append( "[table=13,Desgaste de Peças por Stint,1][big]#[/big]" );
            if ( !ts.getStints().isEmpty() ) {
                for ( String part : Car.MNEM_PTBR ) {
                    builder.append( "[c][big]" );
                    builder.append( part );
                    builder.append( "[/big]" );
                }
                builder.append( "[c][big]Prioridade[/big]\n" );

                int i = 0;
                int[] totalWear = new int[Car.PARTS_COUNT];
                boolean hasAllStints = true;
                for ( TestStint stint : ts.getStints() ) {
                    builder.append( String.format( "[c][big]%d[/big]", (i+1) ) );
                    if ( stint.getCarStart() != null && stint.getCarFinish() != null ) {
                        CarPart[] start = stint.getCarStart().getParts();
                        CarPart[] finish = stint.getCarFinish().getParts();
                        for ( int j = 0; j < Car.PARTS_COUNT; j++ ) {
                            int wear = (int) (finish[j].getWear() - start[j].getWear());
                            totalWear[j] += wear;
                            builder.append( String.format( "[c][big]%2d%% (%d)[/big]", wear, finish[j].getLevel() ) );
                        }
                    } else {
                        hasAllStints = false;
                        for ( int j = 0; j < Car.PARTS_COUNT; j++ ) {
                            builder.append( "[c][big]*[/big]" );
                        }
                    }
                    i++;
                    builder.append( "[c][big]" ).append( stint.getPriority().mnemPtBr ).append( "[/big]\n" );
                }
                if( hasAllStints ) {
                    builder.append( String.format( "[c][big][b]%s[/b][/big]", "Total" ) );
                    for( int j = 0; j < totalWear.length; j++ ) {
                        builder.append( String.format( "[c][big][b]%2d%%[/b][/big]", totalWear[j] ) );
                    }
                    builder.append( "[/table]\n" );
                } else {
                    builder.append( "[/table][i]* Não foi possível calcular o desgaste total devido a falta de dados em alguns stints.[/i]\n" );
                }

            } else {
                builder.append( "[/table][i]Nenhum stint realizado![/i]\n" );
            }
        }

        public void generateTestPoints(StringBuilder builder,
                                       TestSession ts) {
            builder.append( "[table=4,Distribuição de pontos atual,1]" );
            builder.append( String.format( "[big]%-25s[/big][c][big]P[/big][c][big]H[/big][c][big]A[/big]", "Tipo de pontos" ) );
            builder.append( String.format( "[c][big][b]%-25s[/b][/big][c][big]%5.1f[/big][c][big]%5.1f[/big][c][big]%5.1f[/big]\n", 
                                           "Pontos de Teste",
                                           ts.getTestPoints().getPd(),
                                           ts.getTestPoints().getHd(),
                                           ts.getTestPoints().getAd() ) );
            builder.append( String.format( "[c][big][b]%-25s[/b][/big][c][big]%5.1f[/big][c][big]%5.1f[/big][c][big]%5.1f[/big]\n", 
                                           "Pontos de P&D",
                                           ts.getRdPoints().getPd(),
                                           ts.getRdPoints().getHd(),
                                           ts.getRdPoints().getAd() ) );
            builder.append( String.format( "[c][big][b]%-25s[/b][/big][c][big]%5.1f[/big][c][big]%5.1f[/big][c][big]%5.1f[/big]\n", 
                                           "Pontos de Engenharia",
                                           ts.getEngPoints().getPd(),
                                           ts.getEngPoints().getHd(),
                                           ts.getEngPoints().getAd() ) );
            builder.append( String.format( "[c][big][b]%-25s[/b][/big][c][big]%5.1f[/big][c][big]%5.1f[/big][c][big]%5.1f[/big]\n", 
                                           "Pontos de Característica",
                                           ts.getCcPoints().getPd(),
                                           ts.getCcPoints().getHd(),
                                           ts.getCcPoints().getAd() ) );
            builder.append( "[/table]" );
        }
        
        public void generateDriver(StringBuilder builder,
                                   Race race) {
            Driver driver = race.getDriverStart();
            if ( driver == null ) {
                driver = race.getDriverFinish();
            }
            DriverAttributes att = driver.getAttributes();
            builder.append( String.format( "[table=2,Piloto,0][big]%-15s[/big][c][big]%s" +
                                           "[/big][c][big]%-15s[/big][c][big]%d" +
                                           "[/big][c][big]%-15s[/big][c][big]%d" +
                                           "[/big][c][big]%-15s[/big][c][big]%d" +
                                           "[/big][c][big]%-15s[/big][c][big]%d" +
                                           "[/big][c][big]%-15s[/big][c][big]%d" +
                                           "[/big][c][big]%-15s[/big][c][big]%d" +
                                           "[/big][c][big]%-15s[/big][c][big]%d" +
                                           "[/big][c][big]%-15s[/big][c][big]%d" +
                                           "[/big][c][big]%-15s[/big][c][big]%d" +
                                           "[/big][c][big]%-15s[/big][c][big]%d" +
                                           "[/big][c][big]%-15s[/big][c][big]%d" +
                                           "[/big][c][big]%-15s[/big][c][big]%d" +
                                           "[/big][c][big]%-15s[/big][c][big]%d" +
                                           "[/big][c][big]%-15s[/big][c][big]%s[/big]",
                                           "Nome", driver.getName(),
                                           "Total", att.getOverall(),
                                           "Concentração", att.getConcentration(),
                                           "Talento", att.getTalent(),
                                           "Aggressividade", att.getAggressiveness(),
                                           "Experiência", att.getExperience(),
                                           "Conhecimento Tecnico", att.getTechInsight(),
                                           "Resistência", att.getStamina(),
                                           "Carisma", att.getCharisma(),
                                           "Motivação", att.getMotivation(),
                                           "Reputação", att.getReputation(),
                                           "Peso", att.getWeight(),
                                           "Idade", att.getAge(),
                                           "Zona de satisfação", driver.getSatisfactionZone(),
                                           "Pistas Favoritas", driver.getFavoriteTracksNames().toString().replaceAll("[\\[\\]]", "") ) );
            builder.append( "[/table]" );
        }

        public void generateTD(StringBuilder builder,
                               Race race) {
            TechDirector td = race.getTDStart();
            if ( td == null ) {
                builder.append( String.format( "[table=2,Diretor Técnico,0][big]%-15s[/big][c][big]%s" +
                                               "[/big][c][big]%-15s[/big][c][big]%s" +
                                               "[/big][c][big]%-15s[/big][c][big]%s" +
                                               "[/big][c][big]%-15s[/big][c][big]%s" +
                                               "[/big][c][big]%-15s[/big][c][big]%s" +
                                               "[/big][c][big]%-15s[/big][c][big]%s" +
                                               "[/big][c][big]%-15s[/big][c][big]%s" +
                                               "[/big][c][big]%-15s[/big][c][big]%s" +
                                               "[/big][c][big]%-15s[/big][c][big]%s" +
                                               "[/big][c][big]%-15s[/big][c][big]%s" +
                                               "[/big][c][big]%-15s[/big][c][big]%s" +
                                               "[/big][c][big]%-15s[/big][c][big]%s" +
                                               "[/big][c][big]%-15s[/big][c][big]%s" +
                                               "[/big][c][big]%-15s[/big][c][big]%s" +
                                               "[/big][c][big]%-15s[/big][c][big]%s[/big][/table]",
                                               "Nome", "Não contratado",
                                               "Total", "",
                                               "Liderança", "",
                                               "P&D Mecânico", "",
                                               "P&D Eletrônico", "",
                                               "P&D Aerodinâmico", "",
                                               "Experiência", "",
                                               "Coord. de Pit", "",
                                               "Motivação", "",
                                               "Idade", "",
                                               "ZS Asas", "",
                                               "ZS Motor", "",
                                               "ZS Freio", "",
                                               "ZS Câmbio", "",
                                               "ZS Suspensão", "" ) );
            } else {
                TDAttributes att = td.getAttributes();
                Driver driver = race.getDriverStart();
                if ( driver == null ) {
                    driver = race.getDriverFinish();
                }

                builder.append( String.format( "[table=2,Diretor Técnico,0][big]%-15s[/big][c][big]%s" +
                                               "[/big][c][big]%-15s[/big][c][big]%d" +
                                               "[/big][c][big]%-15s[/big][c][big]%d" +
                                               "[/big][c][big]%-15s[/big][c][big]%d" +
                                               "[/big][c][big]%-15s[/big][c][big]%d" +
                                               "[/big][c][big]%-15s[/big][c][big]%d" +
                                               "[/big][c][big]%-15s[/big][c][big]%d" +
                                               "[/big][c][big]%-15s[/big][c][big]%d" +
                                               "[/big][c][big]%-15s[/big][c][big]%d" +
                                               "[/big][c][big]%-15s[/big][c][big]%d" +
                                               "[/big][c][big]%-15s[/big][c][big]%d" +
                                               "[/big][c][big]%-15s[/big][c][big]%d" +
                                               "[/big][c][big]%-15s[/big][c][big]%d" +
                                               "[/big][c][big]%-15s[/big][c][big]%d" +
                                               "[/big][c][big]%-15s[/big][c][big]%d[/big][/table]",
                                               "Nome", td.getName(),
                                               "Total", att.getOverall(),
                                               "Liderança", att.getLeadership(),
                                               "P&D Mecânico", att.getRdMech(),
                                               "P&D Eletrônico", att.getRdElect(),
                                               "P&D Aerodinâmico", att.getRdAero(),
                                               "Experiência", att.getExperience(),
                                               "Coord. de Pit", att.getPitCoord(),
                                               "Motivação", att.getMotivation(),
                                               "Idade", att.getAge(),
                                               "ZS Asas", driver.getSatisfactionZone() + td.getWingsSZ(),
                                               "ZS Motor", driver.getSatisfactionZone() + td.getEngineSZ(),
                                               "ZS Freio", driver.getSatisfactionZone() + td.getBrakesSZ(),
                                               "ZS Câmbio", driver.getSatisfactionZone() + td.getGearboxSZ(),
                                               "ZS Suspensão", driver.getSatisfactionZone() + td.getSuspensionSZ() ) );
            }
        }

        public void generateFacilities(StringBuilder builder,
                Race race) {
            Facilities f = race.getFacilities();
            if (f == null) {
                builder.append(String.format("[table=2,Pessoal & Instalações,0][big]%-15s[/big][c][big]%s" +
                        "[/big][c][big]%-15s[/big][c][big]%s" +
                        "[/big][c][big]%-15s[/big][c][big]%s" +
                        "[/big][c][big]%-15s[/big][c][big]%s" +
                        "[/big][c][big]%-15s[/big][c][big]%s" +
                        "[/big][c][big]%-15s[/big][c][big]%s" +
                        "[/big][c][big]%-15s[/big][c][big]%s" +
                        "[/big][c][big]%-15s[/big][c][big]%s" +
                        "[/big][c][big]%-15s[/big][c][big]%s" +
                        "[/big][c][big]%-15s[/big][c][big]%s" +
                        "[/big][c][big]%-15s[/big][c][big]%s" +
                        "[/big][c][big]%-15s[/big][c][big]%s" +
                        "[/big][c][big]%-15s[/big][c][big]%s" +
                        "[/big][c][big]%-15s[/big][c][big]%s[/big][/table]",
                        "Total", "Não disponível",
                        "Experiência", "",
                        "Motivação", "",
                        "Habilidade Técnica", "",
                        "Tolerância à Pressão", "",
                        "Concentração", "",
                        "Eficiência", "",
                        "Túnel de Vento", "",
                        "Centro de Pits", "",
                        "Oficina de P&D", "",
                        "Centro de Concepção de P&D", "",
                        "Oficina de Engenharia", "",
                        "Laboratório Químico", "",
                        "Comercial", ""));
            } else {
                builder.append(String.format("[table=2,Pessoal & Instalações,0][big]%-15s[/big][c][big]%d (NMT = %d)" +
                        "[/big][c][big]%-15s[/big][c][big]%d" +
                        "[/big][c][big]%-15s[/big][c][big]%d" +
                        "[/big][c][big]%-15s[/big][c][big]%d" +
                        "[/big][c][big]%-15s[/big][c][big]%d" +
                        "[/big][c][big]%-15s[/big][c][big]%d" +
                        "[/big][c][big]%-15s[/big][c][big]%d" +
                        "[/big][c][big]%-15s[/big][c][big]%d" +
                        "[/big][c][big]%-15s[/big][c][big]%d" +
                        "[/big][c][big]%-15s[/big][c][big]%d" +
                        "[/big][c][big]%-15s[/big][c][big]%d" +
                        "[/big][c][big]%-15s[/big][c][big]%d" +
                        "[/big][c][big]%-15s[/big][c][big]%d" +
                        "[/big][c][big]%-15s[/big][c][big]%d[/big][/table]",
                        "Total", f.getOverall(), f.getMlt(),
                        "Experiência", f.getExperience(),
                        "Motivação", f.getMotivation(),
                        "Habilidade Técnica", f.getTechnical(),
                        "Tolerância à Pressão", f.getStress(),
                        "Concentração", f.getConcentration(),
                        "Eficiência", f.getEfficiency(),
                        "Túnel de Vento", f.getWindtunnel(),
                        "Centro de Pits", f.getPitstop(),
                        "Oficina de P&D", f.getWorkshop(),
                        "Centro de Concepção de P&D", f.getDesign(),
                        "Oficina de Engenharia", f.getEngineering(),
                        "Laboratório Químico", f.getAlloy(),
                        "Comercial", f.getCommercial()));
            }
        }

        
        private String formatTime(Integer time) {
            int seg = time / 60000;
            int sec = (time % 60000) / 1000;
            int ms = time % 1000;
            return String.format( "%d:%02d:%03d",
                                  seg,
                                  sec,
                                  ms );
        }
    }

    public static class TextReportGenerator
            implements
            ReportGenerator {

        public String generate(Manager manager,
                               Season season,
                               Race race,
                               TyreSupplierAttrs supplier) {
            StringBuilder builder = new StringBuilder();
            generateTitle( builder, race, manager );
            TestSession ts = race.getTestSession();
            if ( ts != null ) {
                generateRaceWeather( builder, ts );
                generateStints( builder, ts );
                generateCar( builder, ts );
                generateTestPoints( builder, ts );
                generateTyreSupplier( season, builder );
                generateDriver( builder, race );
                generateTD( builder, race );
                generateFacilities( builder, race );
            }
            builder.append( ">>> Relatório de Testes gerado por GPRO Manager's Toolbox " + GproManager.getVersionString() );
            return builder.toString();
        }

        public void generateTitle(StringBuilder builder,
                                  Race race,
                                  Manager manager) {
            builder.append( String.format( "S%02dR%02d - %s - correndo na %s\n\n", race.getSeasonNumber(), race.getNumber(), race.getTestSession().getTrack().getName(), manager.getGroup() ) );
        }

        public void generateRaceWeather(StringBuilder builder,
                                        TestSession ts) {
            builder.append( String.format( "Clima: %s\nTemperatura: %d°C\nUmidade: %d%%\n\n",
                                           ts.getWeather().portuguese + " " + ts.getWeather().bbCode,
                                           ts.getTemperature(),
                                           ts.getHumidity() ) );
        }

        public void generateStints(StringBuilder builder,
                                   TestSession ts) {
            if ( !ts.getStints().isEmpty() ) {
                builder.append( String.format( "Stints %d/10 - Voltas %d/100\n" +
                                               " # | Voltas | Melhor   | Média    | FWg | RWg | Eng | Bra | Gea | Sus | Pneus    | Combust. | Desg | C. Final | Prioridade\n",
                                               ts.getStintsDone(),
                                               ts.getLapsDone() ) );
                for ( TestStint stint : ts.getStints() ) {
                    builder.append( String.format( "%2d |  %2d/%2d | %8s | %8s | %3d | %3d | %3d | %3d | %3d | %3d | %-8s |  %3d lts | %3d%% | %4d lts | %s\n",
                                                   stint.getNumber(),
                                                   stint.getLapsDone(),
                                                   stint.getLapsPlanned(),
                                                   formatTime( stint.getBestTime() ),
                                                   formatTime( stint.getMeanTime() ),
                                                   stint.getSettings().getFrontWing(),
                                                   stint.getSettings().getRearWing(),
                                                   stint.getSettings().getEngine(),
                                                   stint.getSettings().getBrakes(),
                                                   stint.getSettings().getGear(),
                                                   stint.getSettings().getSuspension(),
                                                   stint.getSettings().getTyre(),
                                                   stint.getFuelStart(),
                                                   stint.getTyresEnd(),
                                                   stint.getFuelEnd(),
                                                   stint.getPriority().mnemPtBr ) );
                }
                builder.append( "\n" );
            } else {
                builder.append( "Stints: Nenhum stint realizado!\n" );
            }
        }

        public void generateCar(StringBuilder builder,
                                TestSession ts) {

            builder.append( "Desgaste de Peças por Stint\n" );
            if ( !ts.getStints().isEmpty() ) {
                builder.append( "#  " );
                for ( String part : Car.MNEM_PTBR ) {
                    builder.append( "   |   " );
                    builder.append( part );
                    
                }
                builder.append( "   | Prioridade \n" );

                int i = 0;
                int[] totalWear = new int[Car.PARTS_COUNT];
                boolean hasAllStints = true;
                for ( TestStint stint : ts.getStints() ) {
                    builder.append( String.format( "%-5d", i+1 ) );
                    if ( stint.getCarStart() != null && stint.getCarFinish() != null ) {
                        CarPart[] start = stint.getCarStart().getParts();
                        CarPart[] finish = stint.getCarFinish().getParts();
                        for ( int j = 0; j < Car.PARTS_COUNT; j++ ) {
                            int wear = (int) (finish[j].getWear() - start[j].getWear());
                            totalWear[j] += wear;
                            builder.append( String.format( " | %2d%% (%d)", wear, finish[j].getLevel() ) );
                        }
                    } else {
                        hasAllStints = false;
                        for ( int j = 0; j < Car.PARTS_COUNT; j++ ) {
                            builder.append( " |  * " );
                        }
                    }
                    i++;
                    builder.append( " | " ).append( stint.getPriority().mnemPtBr ).append( "\n" );
                }
                if( hasAllStints ) {
                    builder.append( String.format( "%-5s", "Total" ) );
                    for( int j = 0; j < totalWear.length; j++ ) {
                        builder.append( String.format( " | %2d%%    ", totalWear[j] ) );
                    }
                    builder.append( "\n\n" );
                } else {
                    builder.append( "* Não foi possível calcular o desgaste total devido a falta de dados em alguns stints.\n\n" );
                }

            } else {
                builder.append( "Nenhum stint realizado!\n" );
            }
        }

        public void generateTestPoints(StringBuilder builder,
                                       TestSession ts) {
            builder.append( "Distribuição de pontos atual:\n" );
            builder.append( String.format( "%-25s |   P   |   H   |   A  \n", "Tipo de pontos" ) );
            builder.append( String.format( "%-25s | %5.1f | %5.1f | %5.1f\n", 
                                           "Pontos de Teste",
                                           ts.getTestPoints().getPd(),
                                           ts.getTestPoints().getHd(),
                                           ts.getTestPoints().getAd() ) );
            builder.append( String.format( "%-25s | %5.1f | %5.1f | %5.1f\n", 
                                           "Pontos de P&D",
                                           ts.getRdPoints().getPd(),
                                           ts.getRdPoints().getHd(),
                                           ts.getRdPoints().getAd() ) );
            builder.append( String.format( "%-25s | %5.1f | %5.1f | %5.1f\n", 
                                           "Pontos de Engenharia",
                                           ts.getEngPoints().getPd(),
                                           ts.getEngPoints().getHd(),
                                           ts.getEngPoints().getAd() ) );
            builder.append( String.format( "%-25s | %5.1f | %5.1f | %5.1f\n\n", 
                                           "Pontos de Característica",
                                           ts.getCcPoints().getPd(),
                                           ts.getCcPoints().getHd(),
                                           ts.getCcPoints().getAd() ) );
        }

        private void generateTyreSupplier(Season season,
                                          StringBuilder builder) {
            builder.append( "Fornecedor de Pneus : " + (season.getSupplier() != null ? season.getSupplier().toString() : "<não disponível>") + " " );
            builder.append( season.getSupplier() != null ? season.getSupplier().getBBCode() : "" );
            builder.append( "\n\n" );
        }

        public void generateDriver(StringBuilder builder,
                                   Race race) {
            Driver driver = race.getDriverStart();
            if ( driver == null ) {
                driver = race.getDriverFinish();
            }
            DriverAttributes att = driver.getAttributes();
            builder.append( String.format( "Piloto\n%-19s : %s\n" +
                                           "%-19s : %d\n" +
                                           "%-19s : %d\n" +
                                           "%-19s : %d\n" +
                                           "%-19s : %d\n" +
                                           "%-19s : %d\n" +
                                           "%-19s : %d\n" +
                                           "%-19s : %d\n" +
                                           "%-19s : %d\n" +
                                           "%-19s : %d\n" +
                                           "%-19s : %d\n" +
                                           "%-19s : %d\n" +
                                           "%-19s : %d\n" +
                                           "%-19s : %d\n" +
                                           "%-19s : %s\n\n",
                                           "Nome", driver.getName(),
                                           "Total", att.getOverall(),
                                           "Concentração", att.getConcentration(),
                                           "Talento", att.getTalent(),
                                           "Aggressividade", att.getAggressiveness(),
                                           "Experiência", att.getExperience(),
                                           "Conh. Tecnico", att.getTechInsight(),
                                           "Resistência", att.getStamina(),
                                           "Carisma", att.getCharisma(),
                                           "Motivação", att.getMotivation(),
                                           "Reputação", att.getReputation(),
                                           "Peso", att.getWeight(),
                                           "Idade", att.getAge(),
                                           "Zona de satisfação", driver.getSatisfactionZone(),
                                           "Pistas Fav.", driver.getFavoriteTracksNames().toString().replaceAll("[\\[\\]]", "") ) );
        }

        public void generateTD(StringBuilder builder,
                               Race race) {
            TechDirector td = race.getTDStart();
            if ( td == null ) {
                builder.append( "Diretor Técnico\nNão contratado\n\n" );
            } else {
                TDAttributes att = td.getAttributes();
                Driver driver = race.getDriverStart();
                if ( driver == null ) {
                    driver = race.getDriverFinish();
                }

                builder.append( String.format( "Diretor Técnico\n%-19s : %s\n" +
                                               "%-19s : %d\n" +
                                               "%-19s : %d\n" +
                                               "%-19s : %d\n" +
                                               "%-19s : %d\n" +
                                               "%-19s : %d\n" +
                                               "%-19s : %d\n" +
                                               "%-19s : %d\n" +
                                               "%-19s : %d\n" +
                                               "%-19s : %d\n" +
                                               "%-19s : %d\n" +
                                               "%-19s : %d\n" +
                                               "%-19s : %d\n" +
                                               "%-19s : %d\n\n",
                                               "Nome", td.getName(),
                                               "Total", att.getOverall(),
                                               "Liderança", att.getLeadership(),
                                               "P&D Mecânico", att.getRdMech(),
                                               "P&D Eletrônico", att.getRdElect(),
                                               "P&D Aerodinâmico", att.getRdAero(),
                                               "Experiência", att.getExperience(),
                                               "Coord. de Pit", att.getPitCoord(),
                                               "Motivação", att.getMotivation(),
                                               "ZS Asas", driver.getSatisfactionZone() + td.getWingsSZ(),
                                               "ZS Motor", driver.getSatisfactionZone() + td.getEngineSZ(),
                                               "ZS Freio", driver.getSatisfactionZone() + td.getBrakesSZ(),
                                               "ZS Câmbio", driver.getSatisfactionZone() + td.getGearboxSZ(),
                                               "ZS Suspensão", driver.getSatisfactionZone() + td.getSuspensionSZ() ) );
            }
        }

        public void generateFacilities(StringBuilder builder,
                Race race) {
            Facilities f = race.getFacilities();
            if (f == null) {
                builder.append("Pessoal & Instalações: <não disponível>\n\n");
            } else {
                builder.append(String.format("Pessoal & Instalações:\n" +
                        "    %-19s : %d (MLT=%d)\n\n" +
                        "    %-19s : %d\n" +
                        "    %-19s : %d\n" +
                        "    %-19s : %d\n" +
                        "    %-19s : %d\n" +
                        "    %-19s : %d\n" +
                        "    %-19s : %d\n\n" +
                        "    %-19s : %d\n" +
                        "    %-19s : %d\n" +
                        "    %-19s : %d\n" +
                        "    %-19s : %d\n" +
                        "    %-19s : %d\n" +
                        "    %-19s : %d\n" +
                        "    %-19s : %d\n\n\n",
                        "Total", f.getOverall(), f.getMlt(),
                        "Experiência", f.getExperience(),
                        "Motivação", f.getMotivation(),
                        "Habilidade Técnica", f.getTechnical(),
                        "Tolerância à Pressão", f.getStress(),
                        "Concentração", f.getConcentration(),
                        "Eficiência", f.getEfficiency(),
                        "Túnel de Vento", f.getWindtunnel(),
                        "Centro de Pits", f.getPitstop(),
                        "Oficina de P&D", f.getWorkshop(),
                        "Centro de Concepção de P&D", f.getDesign(),
                        "Oficina de Engenharia", f.getEngineering(),
                        "Laboratório Químico", f.getAlloy(),
                        "Comercial", f.getCommercial()));
            }
        }

        private String formatTime(Integer time) {
            int seg = time / 60000;
            int sec = (time % 60000) / 1000;
            int ms = time % 1000;
            return String.format( "%d:%02d:%03d",
                                  seg,
                                  sec,
                                  ms );
        }
    }

}
