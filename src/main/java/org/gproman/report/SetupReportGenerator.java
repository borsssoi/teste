package org.gproman.report;

import org.gproman.GproManager;
import org.gproman.calc.PracticeHelper;
import org.gproman.model.Manager;
import org.gproman.model.car.Car;
import org.gproman.model.car.CarPart;
import org.gproman.model.car.PHA;
import org.gproman.model.driver.Driver;
import org.gproman.model.driver.DriverAttributes;
import org.gproman.model.race.Lap;
import org.gproman.model.race.Practice;
import org.gproman.model.race.Race;
import org.gproman.model.race.TestSession;
import org.gproman.model.season.Season;
import org.gproman.model.season.TyreSupplierAttrs;
import org.gproman.model.staff.TDAttributes;
import org.gproman.model.staff.TechDirector;

public class SetupReportGenerator {

    public static class BBReportGenerator
            implements
            ReportGenerator {

        public String generate(Manager manager,
                               Season season,
                               Race race,
                               TyreSupplierAttrs supplier) {
            StringBuilder builder = new StringBuilder();
            generateTitle( builder, race, season );
            generatePractice( builder, race );
            generateQualify( builder, race );
            builder.append( "[table=2,,0]" );
            generateTyreSupplier( season, builder, supplier );
            builder.append( "[c]" );
            generateSetup( builder, race );
            generateFuel( builder, race );
            builder.append( "[/table]" );
            builder.append( "[table=3,,0]" );
            generateRisks( builder, race );
            builder.append( "[c]" );
            generateEnergy(builder, race );
            builder.append( "[c]" );
            generateTyreStrategy( builder, race );
            builder.append( "[c]" );
            generateTestPoints( builder, race.getTestSession() );
            //builder.append( "[/table][table=3,,0]" );
            builder.append( "[c]" );
            generateCar( builder, race );
            builder.append( "[c]" );
            generateDriver( builder, race );
            builder.append( "[c]" );
            generateTD( builder, race );
            builder.append( "[/table]" );
            builder.append( "[right][i]Setup gerado por GPRO Manager's Toolbox " + GproManager.getVersionString() + "[/i][/right][/table]" );
            return builder.toString();
        }

        public void generateTitle(StringBuilder builder,
                                  Race race,
                                  Season season) {
            builder.append( String.format( "[table=1,S%02dR%02d - %s - correndo na %s,0]", race.getSeasonNumber(), race.getNumber(), race.getTrack().getName(), season.getGroupName() ) );
        }

        public void generatePractice(StringBuilder builder,
                                     Race race) {
            if( race.getPractice() != null && ! race.getPractice().getLaps().isEmpty() ) {
                Practice practice = race.getPractice();
                PracticeHelper helper = null;
                if ( race.getForecast()[0] != null && race.getDriverStart() != null &&
                     race.getCarStart() != null && race.getTrack() != null ) {
                    helper = new PracticeHelper( race.getTrack(),
                                                 race.getForecast(),
                                                 race.getPractice(),
                                                 race.getDriverStart(),
                                                 race.getTDStart(),
                                                 race.getCarStart() );
                    helper.update();
                    helper.calculateWingSplit();
                }

                builder.append( "[table=12,Treino,1][big]#[/big][c][big]Tempo Total[/big][c][big]Erro[/big][c][big]Líquido[/big][c][big]FWg[/big][c][big]RWg[/big][c][big]Eng[/big][c][big]Bra[/big][c][big]Gea[/big][c][big]Sus[/big][c][big]Pneus[/big][c][big]Comentários[/big]\n" );
                for ( Lap lap : practice.getLaps() ) {
                    builder.append( String.format( "[c][big]%d[/big][c][big]%8s[/big][c][big]%6.3fs[/big][c][big]%8s[/big][c][big]%3d[/big][c][big]%3d[/big][c][big]%3d[/big][c][big]%3d[/big][c][big]%3d[/big][c][big]%3d[/big][c][big]%s[/big][c][big]%s[/big]\n",
                                                   lap.getNumber(),
                                                   formatTime( lap.getTime() ),
                                                   lap.getMistake() / 1000.0,
                                                   formatTime( lap.getNetTime() ),
                                                   lap.getSettings().getFrontWing(),
                                                   lap.getSettings().getRearWing(),
                                                   lap.getSettings().getEngine(),
                                                   lap.getSettings().getBrakes(),
                                                   lap.getSettings().getGear(),
                                                   lap.getSettings().getSuspension(),
                                                   lap.getSettings().getTyre(),
                                                   "[]".equals( lap.getComments() ) ? "[Car OK]" : lap.getComments().toString() ) );
                }
                if( helper != null ) {
                    builder.append( String.format( "[c][c][c][c][c][big]%3d ± %d[/big][c][big]%3d ± %d[/big][c][big]%3d ± %d[/big][c][big]%3d ± %d[/big][c][big]%3d ± %d[/big][c][big]%3d ± %d[/big][c][c][big]%s[/big]\n",
                                                   helper.getPartSetup()[0].getIdealAdjustment(), helper.getPartSetup()[0].getError(),
                                                   helper.getPartSetup()[1].getIdealAdjustment(), helper.getPartSetup()[1].getError(),
                                                   helper.getPartSetup()[2].getIdealAdjustment(), helper.getPartSetup()[2].getError(),
                                                   helper.getPartSetup()[3].getIdealAdjustment(), helper.getPartSetup()[3].getError(),
                                                   helper.getPartSetup()[4].getIdealAdjustment(), helper.getPartSetup()[4].getError(),
                                                   helper.getPartSetup()[5].getIdealAdjustment(), helper.getPartSetup()[5].getError(),
                                                   "Setup e erros calculados" ) );
                }
                builder.append( "[/table]\n" );
            } else {
                builder.append( "[table=1,Treino,0][big][i][center]Nenhuma volta realizada[/center][/i][/big][/table]\n" );
            }
        }

        public void generateQualify(StringBuilder builder,
                                    Race race) {
            builder.append( "[table=10,Qualificação,1][big]#[/big][c][big]Tempo[/big][c][big]FWg[/big][c][big]RWg[/big][c][big]Eng[/big][c][big]Bra[/big][c][big]Gea[/big][c][big]Sus[/big][c][big]Pneus[/big][c][big]Risco[/big]\n" );
            if( race.getQualify1() != null && race.getQualify1().getLap() != null ) {
                Lap lap = race.getQualify1().getLap();
                builder.append( String.format( "[c][big]1[/big][c][big]%8s[/big][c][big]%3d[/big][c][big]%3d[/big][c][big]%3d[/big][c][big]%3d[/big][c][big]%3d[/big][c][big]%3d[/big][c][big]%s[/big][c][big]%s[/big]\n",
                                               formatTime( lap.getTime() ),
                                               lap.getSettings().getFrontWing(),
                                               lap.getSettings().getRearWing(),
                                               lap.getSettings().getEngine(),
                                               lap.getSettings().getBrakes(),
                                               lap.getSettings().getGear(),
                                               lap.getSettings().getSuspension(),
                                               lap.getSettings().getTyre(),
                                               race.getQualify1().getRiskDescr() != null ? race.getQualify1().getRiskDescr() : "" ) );
            } else {
                builder.append( "[c][big]1[/big][c][c][c][c][c][c][c][c][c][big]Não disponível[/big]\n" );
            }
            if( race.getQualify2() != null && race.getQualify2().getLap() != null ) {
                Lap lap = race.getQualify2().getLap();
                builder.append( String.format( "[c][big]2[/big][c][big]%8s[/big][c][big]%3d[/big][c][big]%3d[/big][c][big]%3d[/big][c][big]%3d[/big][c][big]%3d[/big][c][big]%3d[/big][c][big]%s[/big][c][big]%s[/big]\n",
                                               formatTime( lap.getTime() ),
                                               lap.getSettings().getFrontWing(),
                                               lap.getSettings().getRearWing(),
                                               lap.getSettings().getEngine(),
                                               lap.getSettings().getBrakes(),
                                               lap.getSettings().getGear(),
                                               lap.getSettings().getSuspension(),
                                               lap.getSettings().getTyre(),
                                               race.getQualify2().getRiskDescr() != null ? race.getQualify2().getRiskDescr() : "" ) );
            } else {
                builder.append( "[c][big]2[/big][c][c][c][c][c][c][c][c][c][big]Não disponível[/big]\n" );
            }
            builder.append( "[/table]\n" );
        }

        public void generateSetup(StringBuilder builder,
                                  Race race) {
            builder.append( "[table=6,Ajustes para a Corrida,1][big]FWg[/big][c][big]RWg[/big][c][big]Eng[/big][c][big]Bra[/big][c][big]Gea[/big][c][big]Sus[/big]\n" );
            builder.append( String.format( "[c][big]%3d[/big][c][big]%3d[/big][c][big]%3d[/big][c][big]%3d[/big][c][big]%3d[/big][c][big]%3d[/big]\n",
                                           race.getRaceSettings().getFrontWing(),
                                           race.getRaceSettings().getRearWing(),
                                           race.getRaceSettings().getEngine(),
                                           race.getRaceSettings().getBrakes(),
                                           race.getRaceSettings().getGear(),
                                           race.getRaceSettings().getSuspension() ) );
            builder.append( "[/table]\n" );
        }

        public void generateCar(StringBuilder builder,
                                Race race) {

            builder.append( "[table=3,Carro,1][big]Peça[/big][c][big]Nível[/big][c][big]Desgaste[/big]\n" );
            CarPart[] start = race.getCarStart().getParts();
            for ( int i = 0; i < Car.PARTS_COUNT; i++ ) {
                builder.append( String.format( "[c][big]%-11s[/big][c][big]%d[/big][c][big]%4d%%[/big]",
                                               Car.PARTS[i],
                                               start[i].getLevel(),
                                               (int) start[i].getWear() ) );
            }
            builder.append( "[/table]" );
            PHA pha = race.getCarStart().getPHA();
            builder.append( String.format( "[table=3,,1][big]P[/big][c][big]H[/big][c][big]A[/big][c][big]%3d[/big][c][big]%3d[/big][c][big]%3d[/big][/table]\n",
                                           pha.getP(),
                                           pha.getH(),
                                           pha.getA() ) );
        }

        public void generateFuel(StringBuilder builder,
                                 Race race) {
            builder.append( String.format( "[table=2,Combustível,0][big]Início[/big][c][big]%d lts[/big][c][big]Pits[/big][c][big]%s[/big][/table]\n",
                                           race.getStartingFuel(),
                                           race.getFuelStrategy() != null ? race.getFuelStrategy() + " lts" : "<não disponível>" ) );
        }

        private void generateTyreSupplier(Season season,
                                          StringBuilder builder,
                                          TyreSupplierAttrs supplier ) {
            builder.append( "[table=2,Fornecedor de Pneus,0][big]" + (season.getSupplier() != null ? season.getSupplier().toString() : "<não disponível>") + "[/big]" );
            builder.append( "[c]" ).append( season.getSupplier() != null ? season.getSupplier().getBBCode() : "" );
            if( supplier != null ) {
                builder.append("[c][big]Seco:[/big][c][big]").append(supplier.getDry()).append(" barrinhaVermelho").append(supplier.getDry()).append("[/big]");
                builder.append("[c][big]Molhado:[/big][c][big]").append(supplier.getWet()).append(" barrinhaVermelho").append(supplier.getWet()).append("[/big]");
                builder.append("[c][big]Durabilidade:[/big][c][big]").append(supplier.getDurability()).append(" barrinhaVermelho").append(supplier.getDurability()).append("[/big]");
                builder.append("[c][big]Aquecimento:[/big][c][big]").append(supplier.getWarmup()).append(" barrinhaVerde").append(supplier.getWarmup()).append("[/big]");
                builder.append("[c][big]Pico:[/big][c][big]").append(supplier.getPeak()).append("°").append("[/big]");
            }
            builder.append( "[/table]" );
        }

        public void generateRisks(StringBuilder builder,
                                  Race race) {
            builder.append( String.format( "[table=2,Riscos,0][big]Ultrapassagem[/big][c][big]%d[/big][c][big]Defesa[/big][c][big]%d[/big][c][big]Pista Livre[/big][c][big]%d[/big][c][big]Pista Livre Molhada[/big][c][big]%d[/big][c][big]Defeito[/big][c][big]%d[/big][c][big]Largada[/big][c][big]%s[/big][/table]\n",
                                           race.getRiskOvertake(),
                                           race.getRiskDefend(),
                                           race.getRiskClear(),
                                           race.getRiskClearWet(),
                                           race.getRiskMalfunction(),
                                           race.getRiskStarting() != null ? race.getRiskStarting().portuguese : "<não disponível>" ) );
        }
        
        public void generateEnergy(StringBuilder builder,
                                  Race race) {
            builder.append( String.format( "[table=2,Energia,0][big]Energia inicial[/big][c][big]%s[/big][c][big]Energia final[/big][c][big]%s[/big][c][/table]\n",
                                           race.getEnergiaInicial(),
                                           race.getEnergiaFinal() ));
        }

        public void generateTyreStrategy(StringBuilder builder,
                                         Race race) {
            builder.append( String.format( "[table=2,Estratégia de pneus,0]" +
                                           "[big]No início[/big][c][big]%s[/big][c]" +
                                           "[big]Quando chover[/big][c][big]%s[/big][c]" +
                                           "[big]Quando a pista estiver seca[/big][c][big]%s[/big][c]" +
                                           "[big]Aguardar para parar se começar a chover[/big][c][big]%d[/big][c]" +
                                           "[big]Aguardar para parar se parar de chover[/big][c][big]%d[/big][/table]\n",
                                           race.getTyreAtStart() != null ? race.getTyreAtStart().portuguese : "<não disponível>",
                                           race.getTyreWhenWet() != null ? race.getTyreWhenWet().portuguese : "<não disponível>",
                                           race.getTyreWhenDry() != null ? race.getTyreWhenDry().portuguese : "<não disponível>",
                                           race.getWaitPitWet() != null ? race.getWaitPitWet() : "<não disponível>",
                                           race.getWaitPitDry() != null ? race.getWaitPitDry() : "<não disponível>" ) );
        }
        
        public void generateTestPoints(StringBuilder builder,
                                       TestSession ts) {
            if( ts != null ) {
                builder.append( "[table=4,Distribuição de pontos de teste atual,1]" );
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
            } else {
                builder.append( "[table=1,Distribuição de pontos de teste atual,0][big][b][i]Dados de pontos de teste não disponíveis.[/i][/b][/big][/table]" );
                
            }
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
            generateTitle( builder, race, season );
            generatePractice( builder, race );
            generateQualify( builder, race );
            generateSetup( builder, race );
            generateFuel( builder, race );
            generateTyreSupplier( season, builder, supplier );
            generateRisks( builder, race );
            generateTyreStrategy( builder, race );
            generateCar( builder, race );
            generateTestPoints( builder, race.getTestSession() );
            generateDriver( builder, race );
            generateTD( builder, race );
            builder.append( ">>> Setup gerado por GPRO Manager's Toolbox " + GproManager.getVersionString() );
            return builder.toString();
        }

        public void generateTitle(StringBuilder builder,
                                  Race race,
                                  Season season) {
            builder.append( String.format( "S%02dR%02d - %s - correndo na %s\n\n", race.getSeasonNumber(), race.getNumber(), race.getTrack().getName(), season.getGroupName() ) );
        }

        public void generatePractice(StringBuilder builder,
                                     Race race) {
            if( race.getPractice() != null && ! race.getPractice().getLaps().isEmpty() ) {
                Practice practice = race.getPractice();
                PracticeHelper helper = null;
                if ( race.getForecast()[0] != null && race.getPractice() != null && race.getDriverStart() != null &&
                     race.getCarStart() != null && race.getTrack() != null ) {
                    helper = new PracticeHelper( race.getTrack(),
                                                 race.getForecast(),
                                                 race.getPractice(),
                                                 race.getDriverStart(),
                                                 race.getTDStart(),
                                                 race.getCarStart() );
                    helper.update();
                    helper.calculateWingSplit();
                }

                builder.append( "Treino\n # | Total    | Erro   | Líquido  | FWg | RWg | Eng | Bra | Gea | Sus | Pneus    | Comentários\n" );
                for ( Lap lap : practice.getLaps() ) {
                    builder.append( String.format( "%2d | %8s | %5.3fs | %8s | %3d | %3d | %3d | %3d | %3d | %3d | %-8s | %s\n",
                                                   lap.getNumber(),
                                                   formatTime( lap.getTime() ),
                                                   lap.getMistake() / 1000.0,
                                                   formatTime( lap.getNetTime() ),
                                                   lap.getSettings().getFrontWing(),
                                                   lap.getSettings().getRearWing(),
                                                   lap.getSettings().getEngine(),
                                                   lap.getSettings().getBrakes(),
                                                   lap.getSettings().getGear(),
                                                   lap.getSettings().getSuspension(),
                                                   lap.getSettings().getTyre(),
                                                   "[]".equals( lap.getComments() ) ? "[Car OK]" : lap.getComments().toString() ) );
                }
                if ( helper != null ) {
                    builder.append( String.format( "\nAjuste calculado e margem de erro:\n   FWg    |   RWg    |   Eng    |   Bra    |   Gea    |   Sus   \n" +
                                                   " %3d ± %-2d | %3d ± %-2d | %3d ± %-2d | %3d ± %-2d | %3d ± %-2d | %3d ± %-2d\n",
                                                   helper.getPartSetup()[0].getIdealAdjustment(), helper.getPartSetup()[0].getError(),
                                                   helper.getPartSetup()[1].getIdealAdjustment(), helper.getPartSetup()[1].getError(),
                                                   helper.getPartSetup()[2].getIdealAdjustment(), helper.getPartSetup()[2].getError(),
                                                   helper.getPartSetup()[3].getIdealAdjustment(), helper.getPartSetup()[3].getError(),
                                                   helper.getPartSetup()[4].getIdealAdjustment(), helper.getPartSetup()[4].getError(),
                                                   helper.getPartSetup()[5].getIdealAdjustment(), helper.getPartSetup()[5].getError() ) );
                }
                builder.append( "\n" );
            } else {
                builder.append( "Treino: Nenhuma volta realizada!\n" );
            }
        }

        public void generateQualify(StringBuilder builder,
                                    Race race) {
            builder.append( "Qualificação\n # | Tempo    | FWg | RWg | Eng | Bra | Gea | Sus | Pneus | Risco\n" );
            if( race.getQualify1() != null && race.getQualify1().getLap() != null ) {
                Lap lap = race.getQualify1().getLap();
                builder.append( String.format( " 1 | %8s | %3d | %3d | %3d | %3d | %3d | %3d | %s | %s\n",
                                               formatTime( lap.getTime() ),
                                               lap.getSettings().getFrontWing(),
                                               lap.getSettings().getRearWing(),
                                               lap.getSettings().getEngine(),
                                               lap.getSettings().getBrakes(),
                                               lap.getSettings().getGear(),
                                               lap.getSettings().getSuspension(),
                                               lap.getSettings().getTyre(),
                                               race.getQualify1().getRiskDescr() != null ? race.getQualify1().getRiskDescr() : "" ) );
            } else {
                builder.append(" 1 | Não disponível\n");
            }
            if( race.getQualify2() != null && race.getQualify2().getLap() != null ) {
                Lap lap = race.getQualify2().getLap();
                builder.append( String.format( " 2 | %8s | %3d | %3d | %3d | %3d | %3d | %3d | %s | %s\n",
                                               formatTime( lap.getTime() ),
                                               lap.getSettings().getFrontWing(),
                                               lap.getSettings().getRearWing(),
                                               lap.getSettings().getEngine(),
                                               lap.getSettings().getBrakes(),
                                               lap.getSettings().getGear(),
                                               lap.getSettings().getSuspension(),
                                               lap.getSettings().getTyre(),
                                               race.getQualify2().getRiskDescr() != null ? race.getQualify2().getRiskDescr() : "" ) );
            } else {
                builder.append(" 2 | Não disponível\n");
            }
            builder.append( "\n" );
        }

        public void generateSetup(StringBuilder builder,
                                  Race race) {
            builder.append( "Setup\nFWg | RWg | Eng | Bra | Gea | Sus\n" );
            builder.append( String.format( "%3d | %3d | %3d | %3d | %3d | %3d\n",
                                           race.getRaceSettings().getFrontWing(),
                                           race.getRaceSettings().getRearWing(),
                                           race.getRaceSettings().getEngine(),
                                           race.getRaceSettings().getBrakes(),
                                           race.getRaceSettings().getGear(),
                                           race.getRaceSettings().getSuspension() ) );
            builder.append( "\n" );
        }

        public void generateCar(StringBuilder builder,
                                Race race) {

            builder.append( "Carro\nPeça        | Nvl | Desgaste\n" );
            CarPart[] start = race.getCarStart().getParts();
            for ( int i = 0; i < Car.PARTS_COUNT; i++ ) {
                builder.append( String.format( "%-11s |  %d  | %4d%%\n",
                                               Car.PARTS[i],
                                               start[i].getLevel(),
                                               (int) start[i].getWear() ) );
            }
            builder.append( "\n" );
            PHA pha = race.getCarStart().getPHA();
            builder.append( String.format( " P  |  H  |  A\n%3d | %3d | %3d\n\n",
                                           pha.getP(),
                                           pha.getH(),
                                           pha.getA() ) );
        }

        public void generateTestPoints(StringBuilder builder,
                                       TestSession ts) {
            if( ts != null ) {
                builder.append( "Distribuição de pontos de teste atual:\n" );
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
            } else {
                builder.append( "Distribuição de pontos de teste atual: <não disponível>\n\n" );
            }
        }

        public void generateFuel(StringBuilder builder,
                                 Race race) {
            builder.append( String.format( "Combustível\nInício: %d lts\nPits  : %s\n\n",
                                           race.getStartingFuel(),
                                           race.getFuelStrategy() != null ? race.getFuelStrategy() + " lts" : "<não disponível>" ) );
        }

        private void generateTyreSupplier(Season season,
                                          StringBuilder builder,
                                          TyreSupplierAttrs supplier) {
            builder.append( "Fornecedor de Pneus : " + (season.getSupplier() != null ? season.getSupplier().toString() : "<não disponível>") + " " );
            builder.append( season.getSupplier() != null ? season.getSupplier().getBBCode() : "" ).append("\n");
            if( supplier != null ) {
                builder.append("   Seco: ").append(supplier.getDry()).append("\n");
                builder.append("   Molhado: ").append(supplier.getWet()).append("\n");
                builder.append("   Durabilidade: ").append(supplier.getDurability()).append("\n");
                builder.append("   Aquecimento: ").append(supplier.getWarmup()).append("\n");
                builder.append("   Pico: ").append(supplier.getPeak()).append("°").append("\n");
            }
            builder.append( "\n\n" );
        }

        public void generateRisks(StringBuilder builder,
                                  Race race) {
            builder.append( String.format( "Riscos\nUltrapassagem : %d\nDefesa              : %d\nPista Livre         : %d\nPista Livre Molhada : %d\nDefeito             : %d\nLargada             : %s\n\n",
                                           race.getRiskOvertake(),
                                           race.getRiskDefend(),
                                           race.getRiskClear(),
                                           race.getRiskClearWet(),
                                           race.getRiskMalfunction(),
                                           race.getRiskStarting() != null ? race.getRiskStarting().portuguese : "<não disponível>" ) );
        }

        public void generateTyreStrategy(StringBuilder builder,
                                         Race race) {
            builder.append( String.format( "Estratégia de pneus:\n" +
                                           "No início                               : %s\n" +
                                           "Quando chover                           : %s\n" +
                                           "Quando a pista estiver seca             : %s\n" +
                                           "Aguardar para parar se começar a chover : %d\n" +
                                           "Aguardar para parar se parar de chover  : %d\n\n",
                                           race.getTyreAtStart() != null ? race.getTyreAtStart().portuguese : "<não disponível>",
                                           race.getTyreWhenWet() != null ? race.getTyreWhenWet().portuguese : "<não disponível>",
                                           race.getTyreWhenDry() != null ? race.getTyreWhenDry().portuguese : "<não disponível>",
                                           race.getWaitPitWet() != null ? race.getWaitPitWet() : "<não disponível>",
                                           race.getWaitPitDry() != null ? race.getWaitPitDry() : "<não disponível>" ) );
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
                                               "Idade", att.getAge(),
                                               "ZS Asas", driver.getSatisfactionZone() + td.getWingsSZ(),
                                               "ZS Motor", driver.getSatisfactionZone() + td.getEngineSZ(),
                                               "ZS Freio", driver.getSatisfactionZone() + td.getBrakesSZ(),
                                               "ZS Câmbio", driver.getSatisfactionZone() + td.getGearboxSZ(),
                                               "ZS Suspensão", driver.getSatisfactionZone() + td.getSuspensionSZ() ) );
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
