package com.netapp.kpi.scheduller;

import com.netapp.kpi.services.NetappServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

@Component
public class NetappScheduller {

    @Autowired
    NetappServices netappServices ;
    @Autowired
    private Environment environment;
    //public String PATH_DIR = "/home/aqf_user/oi/netapp/usage/";
    @Value("${netappKpi.homeDir}")
    public String PATH_DIR ;

    //@Scheduled(cron = "0 50 * * * *")
    @Scheduled(fixedRate = 200)
    public void netappJobReader() throws IOException {
        netappServices.netappReader();
        netappJobWriter();
    }


    public void netappJobWriter() throws IOException {
        System.out.println(PATH_DIR);
        File dir = new File(PATH_DIR);
        List<String> outputFileName = new ArrayList<>();
        int n =0;
        for (File file : dir.listFiles()) {
            if (file.getName().startsWith("output")){
                outputFileName.add(file.getName());

                Charset charset = StandardCharsets.UTF_8;
                Path path1 = Paths.get(PATH_DIR+outputFileName.get(n));
                List<String> lines = Files.readAllLines(path1, charset);
                String fileByline = lines.get(lines.size() - 1);
                String netappHostnameFormated = fileByline
                        .replaceAll(":","")
                        .replaceAll(">","")
                        .replaceAll(",","");
                lines.remove(lines.size() - 1);
               Files.write(path1, lines, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
                System.out.println("Netapp Hostname :"+netappHostnameFormated);
                System.out.println("Delete line :"+lines.remove(lines.size() -1 ));
               netappServices.netappWriter(outputFileName.get(n),netappHostnameFormated);
            n++;
            }

        }
    }

}
