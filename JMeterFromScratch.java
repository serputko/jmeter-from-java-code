package com.blazemeter.demo;

import org.apache.jmeter.config.*;
import org.apache.jmeter.config.gui.ArgumentsPanel;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.control.gui.LoopControlPanel;
import org.apache.jmeter.control.gui.TestPlanGui;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.protocol.http.control.CookieManager;
import org.apache.jmeter.protocol.http.control.gui.HttpTestSampleGui;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.sampler.TestAction;
import org.apache.jmeter.sampler.gui.TestActionGui;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.threads.gui.ThreadGroupGui;
import org.apache.jmeter.util.JMeterUtils;

import org.apache.jorphan.collections.HashTree;


import java.io.File;
import java.io.FileOutputStream;

import org.apache.commons.io.FileUtils;
import java.awt.Desktop;

import org.apache.jmeter.control.TransactionController;
import org.apache.jmeter.control.gui.TransactionControllerGui;
import org.apache.jmeter.control.IfController;
import org.apache.jmeter.control.gui.IfControllerPanel;
import org.apache.jorphan.collections.ListedHashTree;
import org.apache.jmeter.report.dashboard.ReportGenerator;
import org.apache.jmeter.testbeans.gui.TestBeanGUI;
import org.apache.jmeter.protocol.http.gui.CookiePanel;


public class JMeterFromScratch {

    public static void main(String[] argv) throws Exception {

        File jmeterHome = new File(System.getProperty("jmeter.home", "D:\\apache-jmeter-4.0"));
        String slash = System.getProperty("file.separator");

        if (jmeterHome.exists()) {
            File jmeterProperties = new File(jmeterHome.getPath() + slash + "bin" + slash + "jmeter.properties");
            if (jmeterProperties.exists()) {
                //JMeter Engine
                StandardJMeterEngine jmeter = new StandardJMeterEngine();

                //JMeter initialization (properties, log levels, locale, etc)
                JMeterUtils.setJMeterHome(jmeterHome.getPath());
                JMeterUtils.loadJMeterProperties(jmeterProperties.getPath());
                JMeterUtils.initLocale();

                // JMeter Test Plan, basically JOrphan HashTree
                ListedHashTree testPlanTree = new ListedHashTree();


                // Cookie Manager
                CookieManager cookieManager = new CookieManager();
                cookieManager.setName("Cookie Manager");
                cookieManager.setProperty(TestElement.TEST_CLASS, CookieManager.class.getName());
                cookieManager.setProperty(TestElement.GUI_CLASS, CookiePanel.class.getName());


                // CSV Data Set Config
                CSVDataSet csvDataSet = new CSVDataSet();
                csvDataSet.setProperty("filename", "D:\\apache-jmeter-4.0\\data.csv");
                csvDataSet.setProperty("delimiter", ",");
                csvDataSet.setProperty("variableNames", "domain,User,Pass");
                //csvDataSet.setRecycle(true);
                //csvDataSet.setStopThread(false);
                csvDataSet.setName("CSV Data");
                csvDataSet.setProperty("shareMode", "shareMode.all");
                csvDataSet.setProperty(TestElement.TEST_CLASS, CSVDataSet.class.getName());
                csvDataSet.setProperty(TestElement.GUI_CLASS, TestBeanGUI.class.getName());


                // Random Variable config element
                RandomVariableConfig randomVariableConfig = new RandomVariableConfig();
                randomVariableConfig.setProperty("variableName","PauseTime");
                randomVariableConfig.setProperty("minimumValue","2000");
                randomVariableConfig.setProperty("maximumValue","5000");
                randomVariableConfig.setProperty("perThread",true);
                randomVariableConfig.setName("Random Time");
                randomVariableConfig.setProperty(TestElement.TEST_CLASS, RandomVariableConfig.class.getName());
                randomVariableConfig.setProperty(TestElement.GUI_CLASS, TestBeanGUI.class.getName());


                // Test Action - Pause
                TestAction pauseAction = new TestAction();
                pauseAction.setDuration("${PauseTime}");
                pauseAction.setAction(1);
                pauseAction.setTarget(0);
                pauseAction.setName("Pause");
                pauseAction.setProperty(TestElement.TEST_CLASS, TestAction.class.getName());
                pauseAction.setProperty(TestElement.GUI_CLASS, TestActionGui.class.getName());


                // First HTTP Sampler - open example.com
                HTTPSamplerProxy examplecomSampler = new HTTPSamplerProxy();
                examplecomSampler.setDomain("example.com");
                examplecomSampler.setPort(80);
                examplecomSampler.setPath("/");
                examplecomSampler.setMethod("GET");
                examplecomSampler.setName("Open example.com");
                examplecomSampler.setProperty(TestElement.TEST_CLASS, HTTPSamplerProxy.class.getName());
                examplecomSampler.setProperty(TestElement.GUI_CLASS, HttpTestSampleGui.class.getName());


                // Second HTTP Sampler - open blazemeter.com
                HTTPSamplerProxy blazemetercomSampler = new HTTPSamplerProxy();
                blazemetercomSampler.setDomain("${domain}");
                blazemetercomSampler.setPort(80);
                blazemetercomSampler.setPath("/");
                blazemetercomSampler.setMethod("GET");
                blazemetercomSampler.setName("${domain}");
                blazemetercomSampler.setProperty(TestElement.TEST_CLASS, HTTPSamplerProxy.class.getName());
                blazemetercomSampler.setProperty(TestElement.GUI_CLASS, HttpTestSampleGui.class.getName());


                // Loop Controller
                LoopController loopController = new LoopController();
                loopController.setLoops(1);
                loopController.setFirst(true);
                loopController.setName("Loop controller");
                loopController.setProperty(TestElement.TEST_CLASS, LoopController.class.getName());
                loopController.setProperty(TestElement.GUI_CLASS, LoopControlPanel.class.getName());
                loopController.initialize();


                //If Controller
                IfController ifController = new IfController();
                ifController.setName("IF CONTROLLER");
                ifController.setCondition("true");
                ifController.setProperty(TestElement.TEST_CLASS, IfController.class.getName());
                ifController.setProperty(TestElement.GUI_CLASS, IfControllerPanel.class.getName());


                // Transaction Controller
                TransactionController transactionController = new TransactionController();
                transactionController.setGenerateParentSample(true);
                transactionController.setName("TRANSACTION CONTROLLER");
                transactionController.setProperty(TestElement.TEST_CLASS, TransactionController.class.getName());
                transactionController.setProperty(TestElement.GUI_CLASS, TransactionControllerGui.class.getName());


                // Thread Group
                ThreadGroup threadGroup = new ThreadGroup();
                threadGroup.setName("Example Thread Group");
                threadGroup.setNumThreads(1);
                threadGroup.setRampUp(1);
                threadGroup.setSamplerController(loopController);
                threadGroup.setProperty(TestElement.TEST_CLASS, ThreadGroup.class.getName());
                threadGroup.setProperty(TestElement.GUI_CLASS, ThreadGroupGui.class.getName());

                // Test Plan
                TestPlan testPlan = new TestPlan("Create JMeter Script From Java Code");
                testPlan.setProperty(TestElement.TEST_CLASS, TestPlan.class.getName());
                testPlan.setProperty(TestElement.GUI_CLASS, TestPlanGui.class.getName());
                testPlan.setUserDefinedVariables((Arguments) new ArgumentsPanel().createTestElement());

                // Construct Test Plan from previously initialized elements
                testPlanTree.add(cookieManager);
                testPlanTree.add(csvDataSet);
                testPlanTree.add(randomVariableConfig);
                testPlanTree.add(testPlan);
                HashTree threadGroupHashTree = testPlanTree.add(testPlan, threadGroup);
                threadGroupHashTree.add(transactionController).add(blazemetercomSampler);
                threadGroupHashTree.add(transactionController).add(examplecomSampler);
                threadGroupHashTree.add(blazemetercomSampler);
                threadGroupHashTree.add(pauseAction);
                threadGroupHashTree.add(examplecomSampler);
                threadGroupHashTree.add(ifController).add(blazemetercomSampler);

                // save generated test plan to JMeter's .jmx file format
                SaveService.saveTree(testPlanTree, new FileOutputStream(jmeterHome + slash + "example.jmx"));

                //add Summarizer output to get test progress in stdout like:
                // summary =      2 in   1.3s =    1.5/s Avg:   631 Min:   290 Max:   973 Err:     0 (0.00%)
                Summariser summer = null;
                String summariserName = JMeterUtils.getPropDefault("summariser.name", "summary");
                if (summariserName.length() > 0) {
                    summer = new Summariser(summariserName);
                }

                // Set directory for HTML report
                String repDir = "D:\\repDir";
                JMeterUtils.setProperty("jmeter.reportgenerator.exporter.html.property.output_dir",repDir);

                // Store execution results into a .jtl file
                String logFile = jmeterHome + slash + "example.jtl";
                FileUtils.forceDelete(new File(logFile)); //delete log file
                ResultCollector logger = new ResultCollector(summer);
                ReportGenerator reportGenerator = new ReportGenerator(logFile, logger); //creating ReportGenerator for creating HTML report
                logger.setFilename(logFile);
                testPlanTree.add(testPlanTree.getArray()[0], logger);


                // Run Test Plan
                jmeter.configure(testPlanTree);
                jmeter.run();

                // Report Generator
                FileUtils.deleteDirectory(new File(repDir));//delete old report
                reportGenerator.generate();

                System.out.println("Test completed. See " + jmeterHome + slash + "example.jtl file for results");
                System.out.println("JMeter .jmx script is available at " + jmeterHome + slash + "example.jmx");

                //Open HTML report in default browser
                File htmlFile = new File(repDir + "\\index.html");
                Desktop.getDesktop().browse(htmlFile.toURI());

                System.exit(0);
            }
        }
        System.err.println("jmeter.home property is not set or pointing to incorrect location");
        System.exit(1);
    }
}
