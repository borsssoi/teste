package org.gproman.report;

import java.util.Iterator;
import java.util.List;

import org.gproman.GproManager;
import org.gproman.model.Manager;
import org.gproman.model.car.Car;
import org.gproman.model.car.CarPart;
import org.gproman.model.car.PHA;
import org.gproman.model.driver.Driver;
import org.gproman.model.driver.DriverAttributes;
import org.gproman.model.race.Lap;
import org.gproman.model.race.Race;
import org.gproman.model.race.Race.Stint;
import org.gproman.model.race.TestSession;
import org.gproman.model.season.Season;
import org.gproman.model.season.TyreSupplierAttrs;
import org.gproman.model.staff.Facilities;
import org.gproman.model.staff.TDAttributes;
import org.gproman.model.staff.TechDirector;

public class TelemetryReportGenerator {

    public static class TextReportGenerator
            implements
            ReportGenerator {

        @Override
        public String generate(Manager manager,
                Season season,
                Race race,
                TyreSupplierAttrs supplier) {
            StringBuilder builder = new StringBuilder();
            generateTitle(builder, race, season);
            generateRisks(builder, race);
            generateSetup(builder, race);
            generateTyreSupplier(season, builder, supplier);
            generateFuel(builder, race);
            generateStints(builder, race);
            generateWear(builder, race);
            generateTestPoints(builder, race.getTestSession());
            generateDriver(builder, race);
            generateTD(builder, race);
            generateFacilities(builder, race);
            generateLaps(builder, race);
            builder.append(">>> Telemetria gerada por GPRO Manager's Toolbox " + GproManager.getVersionString());
            return builder.toString();
        }

        public void generateTitle(StringBuilder builder,
                Race race,
                Season season) {
            builder.append(String.format("S%02dR%02d - %s, correndo na %s\n\n", race.getSeasonNumber(), race.getNumber(), race.getTrack().getName(), season.getGroupName()));
        }

        public void generateRisks(StringBuilder builder,
                Race race) {
            builder.append(String.format("Riscos usados:\nUltrapassagem: %d, Defesa: %d, Pista Livre: %d, Pista Livre Molhada: %d, Defeito: %d\n\n",
                    race.getRiskOvertake(),
                    race.getRiskDefend(),
                    race.getRiskClear(),
                    race.getRiskClearWet(),
                    race.getRiskMalfunction()));
        }

        public void generateSetup(StringBuilder builder,
                Race race) {
            builder.append(String.format("Ajuste usado:\nFWg | RWg | Eng | Bra | Gea | Sus\n%3d | %3d | %3d | %3d | %3d | %3d\n\n",
                    race.getRaceSettings().getFrontWing(),
                    race.getRaceSettings().getRearWing(),
                    race.getRaceSettings().getEngine(),
                    race.getRaceSettings().getBrakes(),
                    race.getRaceSettings().getGear(),
                    race.getRaceSettings().getSuspension()));

        }

        private void generateTyreSupplier(Season season,
                StringBuilder builder,
                TyreSupplierAttrs supplier) {
            builder.append("Fornecedor de Pneu: " + (season.getSupplier() != null ? season.getSupplier() : "<não disponível>") + " - ");
            builder.append(season.getSupplier() != null ? season.getSupplier().getBBCode() : "").append("\n");
            if (supplier != null) {
                builder.append("   Seco: ").append(supplier.getDry()).append("\n");
                builder.append("   Molhado: ").append(supplier.getWet()).append("\n");
                builder.append("   Durabilidade: ").append(supplier.getDurability()).append("\n");
                builder.append("   Aquecimento: ").append(supplier.getWarmup()).append("\n");
                builder.append("   Pico: ").append(supplier.getPeak()).append("°").append("\n");
            }
            builder.append("\n\n");
        }

        public void generateFuel(StringBuilder builder,
                Race race) {
            builder.append(String.format("Combustível: \nInício=%s lts, Fim=%s lts, Consumo da corrida=%s, Eficiencia=%s.\n\n",
                    formatNumber("%d", race.getStartingFuel()),
                    formatNumber("%d", race.getFinishFuel()),
                    formatNumber("%01.1f lts", race.getFuelConsumption()),
                    formatNumber("%01.2f Km/lt", race.getFuelEfficiency())));
        }

        public void generateTestPoints(StringBuilder builder,
                TestSession ts) {
            if (ts != null) {
                builder.append("Distribuição de pontos de teste atual:\n");
                builder.append(String.format("%-25s |   P   |   H   |   A  \n", "Tipo de pontos"));
                builder.append(String.format("%-25s | %5.1f | %5.1f | %5.1f\n",
                        "Pontos de Teste",
                        ts.getTestPoints().getPd(),
                        ts.getTestPoints().getHd(),
                        ts.getTestPoints().getAd()));
                builder.append(String.format("%-25s | %5.1f | %5.1f | %5.1f\n",
                        "Pontos de P&D",
                        ts.getRdPoints().getPd(),
                        ts.getRdPoints().getHd(),
                        ts.getRdPoints().getAd()));
                builder.append(String.format("%-25s | %5.1f | %5.1f | %5.1f\n",
                        "Pontos de Engenharia",
                        ts.getEngPoints().getPd(),
                        ts.getEngPoints().getHd(),
                        ts.getEngPoints().getAd()));
                builder.append(String.format("%-25s | %5.1f | %5.1f | %5.1f\n\n",
                        "Pontos de Característica",
                        ts.getCcPoints().getPd(),
                        ts.getCcPoints().getHd(),
                        ts.getCcPoints().getAd()));
            } else {
                builder.append("Distribuição de pontos de teste atual: <não disponível>\n\n");
            }
        }

        public void generateStints(StringBuilder builder,
                Race race) {
            List<Stint> stints = race.getStints();
            builder.append(String.format("Stints: %d\nVoltas | Motivo                    | Comp   | Pneu (   Usado,    noBad,    Total) | Temperat | Umidade  | Sobra Combustível | Reab      | Tempo\n",
                    stints.size()));
            double previousFuel = race.getStartingFuel();
            for (Stint st : stints) {
                double fuelLeft = "Race end".equalsIgnoreCase(st.getPitReason()) ? race.getFinishFuel() : st.getFuelLeft() * 1.8;
                double usedFuel = previousFuel - fuelLeft;
                builder.append(String.format("%2d-%2d  | %-25s | %-6s | %3d%% (%6.2fKm, %6.2fKm, %6.2fKm) |  %2.2f°C |  %2.2f%%  | %5.1fl (%1.2fKm/l) | %-9s | %s\n",
                        st.getInitialLap(),
                        st.getFinalLap(),
                        st.getPitReason(),
                        st.getTyre() != null ? st.getTyre() + ":" : "",
                        st.getTyreLeft(),
                        st.getTyreUsed(),
                        st.getTyreNoBad(),
                        st.getTyreDurability(),
                        st.getAvgTemp(),
                        st.getAvgHum(),
                        fuelLeft,
                        st.getTyreUsed() / usedFuel,
                        st.getRefueledTo() != null ? st.getRefueledTo() + " lts" : "No Refill",
                        st.getPitTime() != null ? String.format("%6.3fs", st.getPitTime() / 1000.0) : "-"));
                previousFuel = st.getRefueledTo() != null ? st.getRefueledTo() : fuelLeft;
            }
            builder.append("\n");
        }

        public void generateWear(StringBuilder builder,
                Race race) {
            boolean c1 = race.getCarStart() != null;
            boolean c2 = race.getCarFinish() != null;
            PHA pha = c1 ? race.getCarStart().getPHA() : (c2 ? race.getCarFinish().getPHA() : null);
            builder.append(String.format("Carro:\n P  |  H  |  A \n%3d | %3d | %3d\n\nParte Nível       | Início |  Fim   | Desgaste\n",
                    pha.getP(),
                    pha.getH(),
                    pha.getA()));
            CarPart[] start = c1 ? race.getCarStart().getParts() : null;
            CarPart[] end = c2 ? race.getCarFinish().getParts() : null;
            boolean addWarning = false;
            if (start == null) {
                for (int i = 0; i < Car.PARTS_COUNT; i++) {
                    builder.append(String.format("%-17s |        |  %4d%%  |\n",
                            String.format("%s %d", Car.PARTS[i], end[i].getLevel()),
                            (int) end[i].getWear()));
                }
            } else {
                for (int i = 0; i < Car.PARTS_COUNT; i++) {
                    String wear = "   *";
                    if (c1 && c2 && (int) end[i].getWear() < 99) {
                        wear = String.format("%3d%%", c1 && c2 ? (int) end[i].getWear() - (int) start[i].getWear() : 0);
                    } else {
                        addWarning = true;
                    }
                    builder.append(String.format("%-17s |  %4d%% |  %4d%% | %s\n",
                            String.format("%s %d", Car.PARTS[i], c1 ? start[i].getLevel() : (c2 ? end[i].getLevel() : 0)),
                            c1 ? (int) start[i].getWear() : 0,
                            c2 ? (int) end[i].getWear() : 0,
                            wear));
                }
            }
            if (addWarning) {
                builder.append("* Desgaste da peça não contabilizado em virtude de desgaste final estar em 99%\n");
            }
            builder.append("\n");
        }

        public void generateDriver(StringBuilder builder,
                Race race) {
            Driver start = race.getDriverStart();
            Driver finish = race.getDriverFinish();
            if (start != null && finish != null) {
                DriverAttributes attStart = start.getAttributes();
                DriverAttributes attFinish = finish.getAttributes();
                builder.append(String.format("Piloto:\n%-15s : %s\n"
                        + "%-15s : %s\n"
                        + "%-15s : %s\n"
                        + "%-15s : %s\n"
                        + "%-15s : %s\n"
                        + "%-15s : %s\n"
                        + "%-15s : %s\n"
                        + "%-15s : %s\n"
                        + "%-15s : %s\n"
                        + "%-15s : %s\n"
                        + "%-15s : %s\n"
                        + "%-15s : %s\n"
                        + "%-15s : %s\n"
                        + "%-15s : %s\n"
                        + "%-15s : %s\n\n",
                        "Nome", start.getName(),
                        "Total", format(attStart.getOverall(), attFinish.getOverall()),
                        "Concentração", format(attStart.getConcentration(), attFinish.getConcentration()),
                        "Talento", format(attStart.getTalent(), attFinish.getTalent()),
                        "Agressividade", format(attStart.getAggressiveness(), attFinish.getAggressiveness()),
                        "Experiência", format(attStart.getExperience(), attFinish.getExperience()),
                        "Conhec. Técnico", format(attStart.getTechInsight(), attFinish.getTechInsight()),
                        "Resistência", format(attStart.getStamina(), attFinish.getStamina()),
                        "Carisma", format(attStart.getCharisma(), attFinish.getCharisma()),
                        "Motivação", format(attStart.getMotivation(), attFinish.getMotivation()),
                        "Reputação", format(attStart.getReputation(), attFinish.getReputation()),
                        "Peso", format(attStart.getWeight(), attFinish.getWeight()),
                        "Idade", format(attStart.getAge(), attFinish.getAge()),
                        "Zona de satisfação", format(start.getSatisfactionZone(), finish.getSatisfactionZone()),
                        "Pistas Favoritas", start.getFavoriteTracksNames().toString().replaceAll("[\\[\\]]", "")));
            } else if (start != null || finish != null) {
                Driver driver = start != null ? start : finish;
                DriverAttributes att = driver.getAttributes();
                builder.append(String.format("Piloto:\n%-15s : %s\n"
                        + "%-15s : %d\n"
                        + "%-15s : %d\n"
                        + "%-15s : %d\n"
                        + "%-15s : %d\n"
                        + "%-15s : %d\n"
                        + "%-15s : %d\n"
                        + "%-15s : %d\n"
                        + "%-15s : %d\n"
                        + "%-15s : %d\n"
                        + "%-15s : %d\n"
                        + "%-15s : %d\n"
                        + "%-15s : %d\n"
                        + "%-15s : %d\n"
                        + "%-15s : %s\n\n",
                        "Nome", driver.getName(),
                        "Total", att.getOverall(),
                        "Concentração", att.getConcentration(),
                        "Talento", att.getTalent(),
                        "Agressividade", att.getAggressiveness(),
                        "Experiencia", att.getExperience(),
                        "Conh. Técnico", att.getTechInsight(),
                        "Resistência", att.getStamina(),
                        "Carisma", att.getCharisma(),
                        "Motivação", att.getMotivation(),
                        "Reputação", att.getReputation(),
                        "Peso", att.getWeight(),
                        "Idade", att.getAge(),
                        "Zona de satisfação", driver.getSatisfactionZone(),
                        "Pistas Fav.", driver.getFavoriteTracksNames().toString()));
            } else {
                builder.append("[table=1,Piloto,0][big][i]Dados do Piloto não disponíveis[/i][/big][/table]");
            }
        }

        public void generateTD(StringBuilder builder,
                Race race) {
            TechDirector td = race.getTDStart();
            if (td == null) {
                builder.append("Diretor Técnico\nNão contratado\n\n");
            } else {
                TDAttributes att = td.getAttributes();
                Driver driver = race.getDriverStart();
                if (driver == null) {
                    driver = race.getDriverFinish();
                }

                builder.append(String.format("Diretor Técnico\n%-19s : %s\n"
                        + "%-19s : %d\n"
                        + "%-19s : %d\n"
                        + "%-19s : %d\n"
                        + "%-19s : %d\n"
                        + "%-19s : %d\n"
                        + "%-19s : %d\n"
                        + "%-19s : %d\n"
                        + "%-19s : %d\n"
                        + "%-19s : %d\n"
                        + "%-19s : %d\n"
                        + "%-19s : %d\n"
                        + "%-19s : %d\n"
                        + "%-19s : %d\n"
                        + "%-19s : %d\n\n",
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
                        "ZS Suspensão", driver.getSatisfactionZone() + td.getSuspensionSZ()));
            }
        }

        public void generateFacilities(StringBuilder builder,
                Race race) {
            Facilities f = race.getFacilities();
            if (f == null) {
                builder.append("Pessoal & Instalações: <não disponível>\n\n");
            } else {
                builder.append(String.format("Pessoal & Instalações:\n"
                        + "    %-19s : %d (MLT=%d)\n\n"
                        + "    %-19s : %d\n"
                        + "    %-19s : %d\n"
                        + "    %-19s : %d\n"
                        + "    %-19s : %d\n"
                        + "    %-19s : %d\n"
                        + "    %-19s : %d\n\n"
                        + "    %-19s : %d\n"
                        + "    %-19s : %d\n"
                        + "    %-19s : %d\n"
                        + "    %-19s : %d\n"
                        + "    %-19s : %d\n"
                        + "    %-19s : %d\n"
                        + "    %-19s : %d\n\n\n",
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

        private String format(int start, int end) {
            int delta = end - start;
            if (delta > 0) {
                return String.format("%d %+d = %d", start, delta, end);
            } else if (delta < 0) {
                return String.format("%d %+d = %d", start, delta, end);
            } else {
                return String.format("%d", start);
            }
        }

        public void generateLaps(StringBuilder builder,
                Race race) {
            List<Lap> laps = race.getLaps();
            double lapDistance = race.getTrack().getLapDistance();
            double lapFuelConsumption = race.getFuelEfficiency() != null ? lapDistance / race.getFuelEfficiency() : -1;
            double fuel = race.getStartingFuel();

            Iterator<Stint> it = race.getStints().iterator();
            Stint stint = it.hasNext() ? it.next() : null;
            double tyre = stint != null ? stint.getTyreDurability() : -1;

            builder.append("Voltas:\n # | Tempo    | Pos | Composto | Clima            | Tem | Umi | Comb* | Pneu* | Eventos\n");
            for (Lap lap : laps) {
                builder.append(String.format("%2d | %-8s | %3d | %-8s | %-16s | %2dC | %2d%% | %-5s | %-5s | %s\n",
                        lap.getNumber(),
                        formatTime(lap.getTime()),
                        lap.getPosition(),
                        lap.getSettings().getTyre() != null ? lap.getSettings().getTyre().toString() : " ",
                        lap.getWeather().toString(),
                        lap.getTemperature(),
                        lap.getHumidity(),
                        (stint != null && lapFuelConsumption > 0) ? String.format("%3d%%", Math.round((fuel * 100) / 180)) : "-",
                        stint != null ? String.format("%3d%%", Math.round((tyre * 100) / stint.getTyreDurability())) : "-",
                        lap.getEvents()));
                if (stint != null) {
                    // the order here is important: first we check if we are moving to the next stint
                    if (lap.getNumber() == stint.getFinalLap()) {
                        if (it.hasNext()) {
                            fuel = stint.getRefueledTo() != null ? stint.getRefueledTo() : ((double) stint.getFuelLeft() * 1.8);
                            stint = it.next();
                            tyre = stint != null ? stint.getTyreDurability() : -1;
                        } else {
                            stint = null;
                            tyre = -1;
                        }
                    }
                    // then we subtract the consumption
                    fuel -= lapFuelConsumption;
                    tyre -= lapDistance;
                }
            }
            if (lapFuelConsumption < 0) {
                builder.append("\n* Não foi possível interpolar o gasto de combustível de forma confiável.");
            }
            builder.append("\n* Os valores de gasto de combustível e pneu por volta são médias aritméticas simples calculadas para simples referência. A troca de clima durante a corrida causa discrepâncias nestes valores.\n\n");
        }
    }

    public static class BBReportGenerator
            implements
            ReportGenerator {

        public String generate(Manager manager,
                Season season,
                Race race,
                TyreSupplierAttrs supplier) {
            StringBuilder builder = new StringBuilder();
            generateTitle(builder, race, season);
            builder.append("[table=2]");
            generateFuel(builder, race);
            builder.append("[c]");
            generateRisks(builder, race);
            builder.append("[c]");
            generateTyreSupplier(season, builder, supplier);
            builder.append("[c]");
            generateSetup(builder, race);
            generateTestPoints(builder, race.getTestSession());
            builder.append("[/table]");
            generateStints(builder, race);
            builder.append("[table=2]\n");
            generateWear(builder, race);
            builder.append("[c]");
            generateFacilities(builder, race);
            builder.append("[c]");
            generateDriver(builder, race);
            builder.append("[c]");
            generateEnergy(builder, race);
            builder.append("[c]");
            generateTD(builder, race);
            builder.append("[/table]");
            generateLaps(builder, race);
            builder.append("[right][i]Telemetria gerada por GPRO Manager's Toolbox " + GproManager.getVersionString() + "[/i][/right][/table]");
            return builder.toString();
        }

        public void generateTitle(StringBuilder builder,
                Race race,
                Season season) {
            builder.append(String.format("[table=1,S%02dR%02d - %s - correndo na %s,0]", race.getSeasonNumber(), race.getNumber(), race.getTrack().getName(), season.getGroupName()));
        }

        public void generateFuel(StringBuilder builder,
                Race race) {
            builder.append(String.format("[table=2,Combustível,0][big]Início[/big][c][big]%s[/big][c][big]Fim[/big][c][big]%s[/big][c][big]Consumo na Corrida[/big][c][big]%s[/big][c][big]Eficiência[/big][c][big]%s[/big][/table]\n",
                    formatNumber("%d", race.getStartingFuel()),
                    formatNumber("%d", race.getFinishFuel()),
                    formatNumber("%01.1f lts", race.getFuelConsumption()),
                    formatNumber("%01.2f Km/lt", race.getFuelEfficiency())));
        }

        public void generateEnergy(StringBuilder builder,
                Race race) {
            builder.append(String.format("[table=2,Energia,0][big]Início[/big][c][big]%s[/big][c][big]Fim[/big][c][big]%s[/big][/table]\n",
                    race.getEnergiaInicial(),
                    race.getEnergiaFinal()));
                    
                    //formatNumber("%d", race.getEnergiaInicial() == null ?  race.getEnergiaInicial() : 0 - race.getEnergiaFinal() <=0 ? race.getEnergiaFinal() : 0)));
        }

        public void generateRisks(StringBuilder builder,
                Race race) {
            builder.append(String.format("[table=2,Riscos usados,0][big]Ultrapassagem[/big][c][big]%d[/big][c][big]Defesa[/big][c][big]%d[/big][c][big]Pista Livre[/big][c][big]%d[/big][c][big]Pista Livre Molhada[/big][c][big]%d[/big][c][big]Defeito[/big][c][big]%d[/big][/table]\n",
                    race.getRiskOvertake(),
                    race.getRiskDefend(),
                    race.getRiskClear(),
                    race.getRiskClearWet(),
                    race.getRiskMalfunction()));
        }

        public void generateSetup(StringBuilder builder,
                Race race) {
            builder.append(String.format("[table=6,Ajuste usado,1][big]FWg[/big][c][big]RWg[/big][c][big]Eng[/big][c][big]Bra[/big][c][big]Gea[/big][c][big]Sus[/big][c][big]%3d[/big][c][big]%3d[/big][c][big]%3d[/big][c][big]%3d[/big][c][big]%3d[/big][c][big]%3d[/big][/table]",
                    race.getRaceSettings().getFrontWing(),
                    race.getRaceSettings().getRearWing(),
                    race.getRaceSettings().getEngine(),
                    race.getRaceSettings().getBrakes(),
                    race.getRaceSettings().getGear(),
                    race.getRaceSettings().getSuspension()));

        }

        private void generateTyreSupplier(Season season,
                StringBuilder builder,
                TyreSupplierAttrs supplier) {
            builder.append("[table=2,Fornecedor de Pneu,0][big]" + (season.getSupplier() != null ? season.getSupplier() : "<não disponível>") + "[/big]");
            builder.append("[c]").append(season.getSupplier() != null ? season.getSupplier().getBBCode() : "");
            if (supplier != null) {
                builder.append("[c][big]Seco:[/big][c][big]").append(supplier.getDry()).append(" barrinhaVermelho").append(supplier.getDry()).append("[/big]");
                builder.append("[c][big]Molhado:[/big][c][big]").append(supplier.getWet()).append(" barrinhaVermelho").append(supplier.getWet()).append("[/big]");
                builder.append("[c][big]Durabilidade:[/big][c][big]").append(supplier.getDurability()).append(" barrinhaVermelho").append(supplier.getDurability()).append("[/big]");
                builder.append("[c][big]Aquecimento:[/big][c][big]").append(supplier.getWarmup()).append(" barrinhaVerde").append(supplier.getWarmup()).append("[/big]");
                builder.append("[c][big]Pico:[/big][c][big]").append(supplier.getPeak()).append("°").append("[/big]");
            }
            builder.append("[/table]");
        }

        public void generateTestPoints(StringBuilder builder,
                TestSession ts) {
            if (ts != null) {
                builder.append("[table=4,Distribuição de pontos de teste atual,1]");
                builder.append(String.format("[big]%-25s[/big][c][big]P[/big][c][big]H[/big][c][big]A[/big]", "Tipo de pontos"));
                builder.append(String.format("[c][big][b]%-25s[/b][/big][c][big]%5.1f[/big][c][big]%5.1f[/big][c][big]%5.1f[/big]\n",
                        "Pontos de Teste",
                        ts.getTestPoints().getPd(),
                        ts.getTestPoints().getHd(),
                        ts.getTestPoints().getAd()));
                builder.append(String.format("[c][big][b]%-25s[/b][/big][c][big]%5.1f[/big][c][big]%5.1f[/big][c][big]%5.1f[/big]\n",
                        "Pontos de P&D",
                        ts.getRdPoints().getPd(),
                        ts.getRdPoints().getHd(),
                        ts.getRdPoints().getAd()));
                builder.append(String.format("[c][big][b]%-25s[/b][/big][c][big]%5.1f[/big][c][big]%5.1f[/big][c][big]%5.1f[/big]\n",
                        "Pontos de Engenharia",
                        ts.getEngPoints().getPd(),
                        ts.getEngPoints().getHd(),
                        ts.getEngPoints().getAd()));
                builder.append(String.format("[c][big][b]%-25s[/b][/big][c][big]%5.1f[/big][c][big]%5.1f[/big][c][big]%5.1f[/big]\n",
                        "Pontos de Característica",
                        ts.getCcPoints().getPd(),
                        ts.getCcPoints().getHd(),
                        ts.getCcPoints().getAd()));
                builder.append("[/table]");
            } else {
                builder.append("[table=1,Distribuição de pontos de teste atual,0][big][b][i]Dados de pontos de teste não disponíveis.[/i][/b][/big][/table]");

            }
        }

        public void generateStints(StringBuilder builder,
                Race race) {
            List<Stint> stints = race.getStints();
            builder.append(String.format("[table=9,Stints: %d,1]Voltas[c]Motivo[c]Composto[c]Pneus (Usado, noBad, Total)[c]Temperatura[c]Umidade[c]Combustível (Eff)[c]Reab.[c]Tempo\n",
                    stints.size()));
            double previousFuel = race.getStartingFuel();
            for (Stint st : stints) {
                double fuelLeft = "Race end".equalsIgnoreCase(st.getPitReason()) ? race.getFinishFuel() : st.getFuelLeft() * 1.8;
                double usedFuel = previousFuel - fuelLeft;
                builder.append(String.format("[c]%2d-%2d[c]%-25s[c]%-6s[c]%3d%% (%6.2fKm, %6.2fKm, %6.2fKm)[c]%2.2f°C[c]%2.2f%%[c]%2.1fl (%1.2fKm/l)[c]%s[c]%s\n",
                        st.getInitialLap(),
                        st.getFinalLap(),
                        st.getPitReason(),
                        st.getTyre() != null ? st.getTyre() + ":" : "",
                        st.getTyreLeft(),
                        st.getTyreUsed(),
                        st.getTyreNoBad(),
                        st.getTyreDurability(),
                        st.getAvgTemp(),
                        st.getAvgHum(),
                        fuelLeft,
                        st.getTyreUsed() / usedFuel,
                        st.getRefueledTo() != null ? st.getRefueledTo() + " lts" : "No Refill",
                        st.getPitTime() != null ? String.format("%6.3fs", st.getPitTime() / 1000.0) : "-"));
                previousFuel = st.getRefueledTo() != null ? st.getRefueledTo() : fuelLeft;
            }
            builder.append("[/table]\n");
        }

        public void generateWear(StringBuilder builder,
                Race race) {

            builder.append("[table=4,Carro,1][big]Parte Nível[/big][c][big]Início[/big][c][big]Fim[/big][c][big]Desgaste[/big]\n");
            boolean c1 = race.getCarStart() != null;
            boolean c2 = race.getCarFinish() != null;
            CarPart[] start = c1 ? race.getCarStart().getParts() : null;
            CarPart[] end = c2 ? race.getCarFinish().getParts() : null;
            boolean addWarning = false;
            if (start == null) {
                for (int i = 0; i < Car.PARTS_COUNT; i++) {
                    builder.append(String.format("[c][big]%s %d[/big][c][c][big]%4d%%[/big][c]",
                            Car.PARTS[i],
                            end[i].getLevel(),
                            (int) end[i].getWear()));
                }
            } else {
                for (int i = 0; i < Car.PARTS_COUNT; i++) {
                    String wear = "   *";
                    if (c1 && c2 && (int) end[i].getWear() < 99) {
                        wear = String.format("%3d%%", c1 && c2 ? (int) end[i].getWear() - (int) start[i].getWear() : 0);
                    } else {
                        addWarning = true;
                    }
                    builder.append(String.format("[c][big]%s %d[/big][c][big]%4d%%[/big][c][big]%4d%%[/big][c][big]%s[/big]",
                            Car.PARTS[i],
                            c1 ? start[i].getLevel() : (c2 ? end[i].getLevel() : 0),
                            c1 ? (int) start[i].getWear() : 0,
                            c2 ? (int) end[i].getWear() : 0,
                            wear));
                }
            }
            builder.append("[/table]");
            if (addWarning) {
                builder.append("[table=1,,0][big][i]* Desgaste da peça não contabilizado em virtude de desgaste final estar em 99%[/i][/big][/table]");
            }
            PHA pha = c1 ? race.getCarStart().getPHA() : (c2 ? race.getCarFinish().getPHA() : null);
            if (pha != null) {
                builder.append(String.format("[table=3,,1][big]P[/big][c][big]H[/big][c][big]A[/big][c][big]%3d[/big][c][big]%3d[/big][c][big]%3d[/big][/table]\n",
                        pha.getP(),
                        pha.getH(),
                        pha.getA()));
            }
        }

        public void generateDriver(StringBuilder builder,
                Race race) {
            Driver start = race.getDriverStart();
            Driver finish = race.getDriverFinish();
            if (start != null && finish != null) {
                DriverAttributes attStart = start.getAttributes();
                DriverAttributes attFinish = finish.getAttributes();
                builder.append(String.format("[table=2,Piloto,0][big]%-15s[/big][c][big]%s"
                        + "[/big][c][big]%-15s[/big][c][big]%s"
                        + "[/big][c][big]%-15s[/big][c][big]%s"
                        + "[/big][c][big]%-15s[/big][c][big]%s"
                        + "[/big][c][big]%-15s[/big][c][big]%s"
                        + "[/big][c][big]%-15s[/big][c][big]%s"
                        + "[/big][c][big]%-15s[/big][c][big]%s"
                        + "[/big][c][big]%-15s[/big][c][big]%s"
                        + "[/big][c][big]%-15s[/big][c][big]%s"
                        + "[/big][c][big]%-15s[/big][c][big]%s"
                        + "[/big][c][big]%-15s[/big][c][big]%s"
                        + "[/big][c][big]%-15s[/big][c][big]%s"
                        + "[/big][c][big]%-15s[/big][c][big]%s"
                        + "[/big][c][big]%-15s[/big][c][big]%s"
                        + "[/big][c][big]%-15s[/big][c][big]%s[/big][/table]",
                        "Nome", start.getName(),
                        "Total", format(attStart.getOverall(), attFinish.getOverall()),
                        "Concentração", format(attStart.getConcentration(), attFinish.getConcentration()),
                        "Talento", format(attStart.getTalent(), attFinish.getTalent()),
                        "Agressividade", format(attStart.getAggressiveness(), attFinish.getAggressiveness()),
                        "Experiência", format(attStart.getExperience(), attFinish.getExperience()),
                        "Conhec. Técnico", format(attStart.getTechInsight(), attFinish.getTechInsight()),
                        "Resistência", format(attStart.getStamina(), attFinish.getStamina()),
                        "Carisma", format(attStart.getCharisma(), attFinish.getCharisma()),
                        "Motivação", format(attStart.getMotivation(), attFinish.getMotivation()),
                        "Reputação", format(attStart.getReputation(), attFinish.getReputation()),
                        "Peso", format(attStart.getWeight(), attFinish.getWeight()),
                        "Idade", format(attStart.getAge(), attFinish.getAge()),
                        "Zona de satisfação", format(start.getSatisfactionZone(), finish.getSatisfactionZone()),
                        "Pistas Favoritas", start.getFavoriteTracksNames().toString().replaceAll("[\\[\\]]", "")));
            } else if (start != null || finish != null) {
                Driver driver = start != null ? start : finish;
                DriverAttributes att = driver.getAttributes();
                builder.append(String.format("[table=2,Piloto,0][big]%-15s[/big][c][big]%s"
                        + "[/big][c][big]%-15s[/big][c][big]%d"
                        + "[/big][c][big]%-15s[/big][c][big]%d"
                        + "[/big][c][big]%-15s[/big][c][big]%d"
                        + "[/big][c][big]%-15s[/big][c][big]%d"
                        + "[/big][c][big]%-15s[/big][c][big]%d"
                        + "[/big][c][big]%-15s[/big][c][big]%d"
                        + "[/big][c][big]%-15s[/big][c][big]%d"
                        + "[/big][c][big]%-15s[/big][c][big]%d"
                        + "[/big][c][big]%-15s[/big][c][big]%d"
                        + "[/big][c][big]%-15s[/big][c][big]%d"
                        + "[/big][c][big]%-15s[/big][c][big]%d"
                        + "[/big][c][big]%-15s[/big][c][big]%d"
                        + "[/big][c][big]%-15s[/big][c][big]%d"
                        + "[/big][c][big]%-15s[/big][c][big]%s[/big][/table]",
                        "Nome", driver.getName(),
                        "Total", att.getOverall(),
                        "Concentração", att.getConcentration(),
                        "Talento", att.getTalent(),
                        "Agressividade", att.getAggressiveness(),
                        "Experiência", att.getExperience(),
                        "Conhec. Técnico", att.getTechInsight(),
                        "Resistência", att.getStamina(),
                        "Carisma", att.getCharisma(),
                        "Motivação", att.getMotivation(),
                        "Reputação", att.getReputation(),
                        "Peso", att.getWeight(),
                        "Idade", att.getAge(),
                        "Zona de satisfação", driver.getSatisfactionZone(),
                        "Pistas Favoritas", driver.getFavoriteTracksNames().toString()));
            } else {
                builder.append("[table=1,Piloto,0][big][i]Dados do Piloto não disponíveis[/i][/big][/table]");
            }
        }

        public void generateTD(StringBuilder builder,
                Race race) {
            TechDirector td = race.getTDStart();
            if (td == null) {
                builder.append(String.format("[table=2,Diretor Técnico,0][big]%-15s[/big][c][big]%s"
                        + "[/big][c][big]%-15s[/big][c][big]%s"
                        + "[/big][c][big]%-15s[/big][c][big]%s"
                        + "[/big][c][big]%-15s[/big][c][big]%s"
                        + "[/big][c][big]%-15s[/big][c][big]%s"
                        + "[/big][c][big]%-15s[/big][c][big]%s"
                        + "[/big][c][big]%-15s[/big][c][big]%s"
                        + "[/big][c][big]%-15s[/big][c][big]%s"
                        + "[/big][c][big]%-15s[/big][c][big]%s"
                        + "[/big][c][big]%-15s[/big][c][big]%s"
                        + "[/big][c][big]%-15s[/big][c][big]%s"
                        + "[/big][c][big]%-15s[/big][c][big]%s"
                        + "[/big][c][big]%-15s[/big][c][big]%s"
                        + "[/big][c][big]%-15s[/big][c][big]%s"
                        + "[/big][c][big]%-15s[/big][c][big]%s[/big][/table]",
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
                        "ZS Suspensão", ""));
            } else {
                TDAttributes att = td.getAttributes();
                Driver driver = race.getDriverStart();
                if (driver == null) {
                    driver = race.getDriverFinish();
                }

                builder.append(String.format("[table=2,Diretor Técnico,0][big]%-15s[/big][c][big]%s"
                        + "[/big][c][big]%-15s[/big][c][big]%d"
                        + "[/big][c][big]%-15s[/big][c][big]%d"
                        + "[/big][c][big]%-15s[/big][c][big]%d"
                        + "[/big][c][big]%-15s[/big][c][big]%d"
                        + "[/big][c][big]%-15s[/big][c][big]%d"
                        + "[/big][c][big]%-15s[/big][c][big]%d"
                        + "[/big][c][big]%-15s[/big][c][big]%d"
                        + "[/big][c][big]%-15s[/big][c][big]%d"
                        + "[/big][c][big]%-15s[/big][c][big]%d"
                        + "[/big][c][big]%-15s[/big][c][big]%d"
                        + "[/big][c][big]%-15s[/big][c][big]%d"
                        + "[/big][c][big]%-15s[/big][c][big]%d"
                        + "[/big][c][big]%-15s[/big][c][big]%d"
                        + "[/big][c][big]%-15s[/big][c][big]%d[/big][/table]",
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
                        "ZS Suspensão", driver.getSatisfactionZone() + td.getSuspensionSZ()));
            }
        }

        public void generateFacilities(StringBuilder builder,
                Race race) {
            Facilities f = race.getFacilities();
            if (f == null) {
                builder.append(String.format("[table=2,Pessoal & Instalações,0][big]%-15s[/big][c][big]%s"
                        + "[/big][c][big]%-15s[/big][c][big]%s"
                        + "[/big][c][big]%-15s[/big][c][big]%s"
                        + "[/big][c][big]%-15s[/big][c][big]%s"
                        + "[/big][c][big]%-15s[/big][c][big]%s"
                        + "[/big][c][big]%-15s[/big][c][big]%s"
                        + "[/big][c][big]%-15s[/big][c][big]%s"
                        + "[/big][c][big]%-15s[/big][c][big]%s"
                        + "[/big][c][big]%-15s[/big][c][big]%s"
                        + "[/big][c][big]%-15s[/big][c][big]%s"
                        + "[/big][c][big]%-15s[/big][c][big]%s"
                        + "[/big][c][big]%-15s[/big][c][big]%s"
                        + "[/big][c][big]%-15s[/big][c][big]%s"
                        + "[/big][c][big]%-15s[/big][c][big]%s[/big][/table]",
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
                builder.append(String.format("[table=2,Pessoal & Instalações,0][big]%-15s[/big][c][big]%d (NMT = %d)"
                        + "[/big][c][big]%-15s[/big][c][big]%d"
                        + "[/big][c][big]%-15s[/big][c][big]%d"
                        + "[/big][c][big]%-15s[/big][c][big]%d"
                        + "[/big][c][big]%-15s[/big][c][big]%d"
                        + "[/big][c][big]%-15s[/big][c][big]%d"
                        + "[/big][c][big]%-15s[/big][c][big]%d"
                        + "[/big][c][big]%-15s[/big][c][big]%d"
                        + "[/big][c][big]%-15s[/big][c][big]%d"
                        + "[/big][c][big]%-15s[/big][c][big]%d"
                        + "[/big][c][big]%-15s[/big][c][big]%d"
                        + "[/big][c][big]%-15s[/big][c][big]%d"
                        + "[/big][c][big]%-15s[/big][c][big]%d"
                        + "[/big][c][big]%-15s[/big][c][big]%d[/big][/table]",
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

        private String format(int start, int end) {
            int delta = end - start;
            if (delta > 0) {
                return String.format("%d [color=#090][b]%+d = %d[/b][/color]", start, delta, end);
            } else if (delta < 0) {
                return String.format("%d [color=#ee4a2d][b]%+d = %d[/b][/color]", start, delta, end);
            } else {
                return String.format("%d", start);
            }
        }

        public void generateLaps(StringBuilder builder,
                Race race) {
            List<Lap> laps = race.getLaps();
            double lapDistance = race.getTrack().getLapDistance();
            double lapFuelConsumption = race.getFuelEfficiency() != null ? lapDistance / race.getFuelEfficiency() : -1;
            double fuel = race.getStartingFuel();

            Iterator<Stint> it = race.getStints().iterator();
            Stint stint = it.hasNext() ? it.next() : null;
            double tyre = stint != null ? stint.getTyreDurability() : -1;

            builder.append("[table=10,Voltas,1][big]#[/big][c][big]Tempo[/big][c][big]Pos[/big][c][big]Composto[/big][c][big]Clima[/big][c][big]Temp[/big][c][big]Umi[/big][c][big]Comb[i]*[/i][/big][c][big]Pneu[i]*[/i][/big][c][big]Eventos[/big]\n");
            for (Lap lap : laps) {
                builder.append(String.format("[c][big]%2d[/big][c][big]%s[/big][c][big]%3d[/big][c][big]%-8s[/big][c][big]%-16s[/big][c][big]%2d°C[/big][c][big]%2d%%[/big][c][big]%s[/big][c][big]%s[/big][c][big]%s[/big]\n",
                        lap.getNumber(),
                        formatTime(lap.getTime()),
                        lap.getPosition(),
                        lap.getSettings().getTyre() != null ? lap.getSettings().getTyre().toString() : " ",
                        lap.getWeather().toString(),
                        lap.getTemperature(),
                        lap.getHumidity(),
                        (stint != null && lapFuelConsumption > 0) ? String.format("%3d%%", Math.round((fuel * 100) / 180)) : "-",
                        stint != null ? String.format("%3d%%", Math.round((tyre * 100) / stint.getTyreDurability())) : "-",
                        lap.getEvents()));
                if (stint != null) {
                    // the order here is important: first we check if we are moving to the next stint
                    if (lap.getNumber() == stint.getFinalLap()) {
                        if (it.hasNext()) {
                            fuel = stint.getRefueledTo() != null ? stint.getRefueledTo() : ((double) stint.getFuelLeft() * 1.8);
                            stint = it.next();
                            tyre = stint != null ? stint.getTyreDurability() : -1;
                        } else {
                            stint = null;
                            tyre = -1;
                        }
                    }
                    // then we subtract the consumption
                    fuel -= lapFuelConsumption;
                    tyre -= lapDistance;
                }
            }
            builder.append("[/table]\n");
            if (lapFuelConsumption < 0) {
                builder.append("[i][b]*[/b]Não foi possível interpolar o gasto de combustível de forma confiável.[/i]\n");
            }
            builder.append("[i][b]*[/b]Os valores de gasto de combustível e pneu por volta são médias aritméticas simples calculadas para simples referência. A troca de clima durante a corrida causa discrepâncias nestes valores.[/i]");
        }

    }

    public static String formatTime(Integer time) {
        if (time > 0) {
            int seg = time / 60000;
            int sec = (time % 60000) / 1000;
            int ms = time % 1000;
            return String.format("%d:%02d:%03d",
                    seg,
                    sec,
                    ms);
        }
        return "-";
    }

    public static String formatNumber(String mask,
            Number val) {
        return val == null ? "<não disponível>" : String.format(mask, val);
    }
}
