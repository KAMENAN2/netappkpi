package com.netapp.kpi.services;

import com.netapp.kpi.dao.OINetappAggrRepository;
import com.netapp.kpi.entities.OCI1NetappAggr;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Service
public class NetappServices {

@Autowired
private OINetappAggrRepository OINetappAggrRepository;
    @Autowired
    private Environment environment;
    //public String PATH_DIR = "/home/aqf_user/oi/netapp/usage/";
    @Value("${netappKpi.homeDir}")
    public String PATH_DIR;

    public void netappWriter(String filepath, String nodeName){

        String SAMPLE_CSV_FILE_PATH = filepath;

        try (
                //Buffer ou Tempon de sauvegarde
                Reader reader = Files.newBufferedReader(Paths.get(PATH_DIR+SAMPLE_CSV_FILE_PATH));
        ) {

            CsvToBean<OCI1NetappAggr> csvToBean = new CsvToBeanBuilder(reader)
                    .withType(OCI1NetappAggr.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            Iterator<OCI1NetappAggr> ControllerBIterator = csvToBean.iterator();

            while (ControllerBIterator.hasNext()) {


                OCI1NetappAggr netappCollectItr = ControllerBIterator.next();
                netappCollectItr.setCreated_at(new Date());
                netappCollectItr.setNodeNameAggregate(nodeName+"." + netappCollectItr.getAggregate());


                OINetappAggrRepository.saveAndFlush(netappCollectItr);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void netappReader() throws IOException {

        File dir = new File(PATH_DIR);
        List<String> textFiles = new ArrayList<>();
        List<String> outputFileName = new ArrayList<>();
        for (File file : dir.listFiles()) {
            if (file.getName().startsWith("netapp")) {
                textFiles.add(file.getName());
                //System.out.println(file.getName());

                File newOutpuFile = new File(PATH_DIR+"output_"+file.getName());
                if (newOutpuFile.createNewFile()) {
                    System.out.println("File created: " + newOutpuFile.getName());
                } else {
                    System.out.println("File already exists.");
                }
            }


            if (file.getName().startsWith("output")){
                outputFileName.add(file.getName());
                //System.out.println(file.getName());
            }


        }
        System.out.println(textFiles.toString());

        for (int i =0 ; i < textFiles.size();i++){
            //System.out.println(i);
            Path netappKpiFile = Paths.get(PATH_DIR+textFiles.get(i));
            Charset charset = StandardCharsets.UTF_8;
            Path path1 = Paths.get(PATH_DIR+ "output_"+textFiles.get(i));
            new FileOutputStream(PATH_DIR+ "output_"+textFiles.get(i)).close();

                //inscription de neaveau en-tete
                Files.write(path1, ("Aggregate,total,used,avail,capacity" + System.lineSeparator()).getBytes(), StandardOpenOption.CREATE,StandardOpenOption.APPEND);

            List<String> lines = Files.readAllLines(netappKpiFile, charset);

            for (int j = 2;j<lines.size();j++){

                if (!lines.get(j).contains("GMT")) {
                    if (!lines.get(j).isEmpty() ){
                        if (!lines.get(j).contains("Aggregate") ){
                            if(!lines.get(j).contains("snapshot")  ){
                                if(!lines.get(j).contains("entries")  ){
                                     if(!lines.get(j).contains("df")  ){

                                    System.out.println(lines.get(j));
                                    String fileByline = lines.get(j);
                                    String lineFormat = fileByline
                                            .replaceAll("\\s+", ",")
                                            .replaceAll("%", "")
                                            .replaceAll("GB", "");
                                    System.out.println(lineFormat);
                                    Files.write(path1, (lineFormat + System.lineSeparator()).getBytes(), StandardOpenOption.CREATE,StandardOpenOption.APPEND);

                }
                    }
                        }
                            }
                                }
                                    }
            }

        }





    }

}
