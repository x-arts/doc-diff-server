package com.did.docdiffserver.simple;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SimpleTest2 {


    private String html = "<table id=\"table1\"><tr><td>序号</td><td>名称</td><td>规格型号</td><td>单位</td><td>数量</td><td>品牌</td><td>备注</td></tr><tr><td>一</td><td>中心侧安全一区设备</td><td></td><td></td><td></td><td></td><td></td></tr><tr><td>1</td><td>数据采集服务器</td><td>CS5260H2</td><td>台</td><td>2</td><td>浪潮</td><td></td></tr><tr><td>2</td><td>时序数据库服务器</td><td>CS5260H2</td><td>台</td><td>2</td><td>浪潮</td><td></td></tr><tr><td>3</td><td>关系数据库服务器</td><td>CS5260H2</td><td>台</td><td>2</td><td>浪潮</td><td></td></tr><tr><td>4</td><td>数据计算服务器</td><td>CS5260H2</td><td>台</td><td>2</td><td>浪潮</td><td></td></tr><tr><td>5</td><td>应用服务器</td><td>CS5260H2</td><td>台</td><td>2</td><td>浪潮</td><td></td></tr><tr><td>6</td><td>磁盘阵列</td><td>AS5500G5-C</td><td>套</td><td>1</td><td>浪潮</td><td></td></tr><tr><td>7</td><td>HA高可用软件</td><td>高可用集群软件</td><td>套</td><td>2</td><td>麒麟</td><td></td></tr><tr><td>8</td><td>时钟服务器</td><td>PTS-10A</td><td>台</td><td>2</td><td>东土</td><td></td></tr><tr><td>9</td><td>汇聚交换机</td><td>S5731-H48T4XC</td><td>台</td><td>2</td><td>华为</td><td></td></tr><tr><td>10</td><td>接入交换机</td><td>S5735-S24T4X</td><td>台</td><td>2</td><td>华为</td><td></td></tr><tr><td>11</td><td>I-II区防火墙</td><td>USG-FW-310-T-NF1608</td><td>台</td><td>1</td><td>启明星辰</td><td></td></tr><tr><td>12</td><td>工作站</td><td>CE520F</td><td>台</td><td>6</td><td>浪潮</td><td></td></tr><tr><td>13</td><td>操作系统服务器版</td><td>服务器版（V10）</td><td>套</td><td>10</td><td>麒麟</td><td></td></tr><tr><td>14</td><td>操作系统工作站版</td><td>工作站版（V10）</td><td>套</td><td>6</td><td>麒麟</td><td></td></tr><tr><td>二</td><td>中心侧安全二区设备</td><td></td><td></td><td></td><td></td><td></td></tr><tr><td>1</td><td>数据服务器</td><td>CS5260H2</td><td>台</td><td>2</td><td>浪潮</td><td></td></tr><tr><td>2</td><td>功能服务器</td><td>CS5260H2</td><td>台</td><td>2</td><td>浪潮</td><td></td></tr><tr><td>3</td><td>汇聚交换机</td><td>S5731-H48T4XC</td><td>台</td><td>2</td><td>华为</td><td></td></tr><tr><td>4</td><td>操作系统服务器版</td><td>服务器版（V10）</td><td>套</td><td>4</td><td>麒麟</td><td></td></tr><tr><td>三</td><td>管理区设备</td><td></td><td></td><td></td><td></td><td></td></tr><tr><td>1</td><td>管理信息区时序数据库服务器</td><td>CS5260H2</td><td>台</td><td>2</td><td>浪潮</td><td></td></tr><tr><td>2</td><td>管理信息区关系数据库服务器</td><td>CS5260H2</td><td>台</td><td>2</td><td>浪潮</td><td></td></tr><tr><td>3</td><td>管理信息区应用服务器</td><td>CS5260H2</td><td>台</td><td>2</td><td>浪潮</td><td></td></tr><tr><td>4</td><td>管理信息区视频监控服务器</td><td>DS-VE22S-B</td><td>台</td><td>2</td><td>海康威视</td><td></td></tr><tr><td>5</td><td>磁盘阵列</td><td>AS5500G5-C</td><td>套</td><td>1</td><td>浪潮</td><td></td></tr><tr><td>6</td><td>HA高可用软件</td><td>高可用集群软件</td><td>套</td><td>1</td><td>麒麟</td><td></td></tr><tr><td>7</td><td>综合视频接入平台</td><td>定制</td><td>套</td><td>1</td><td>海康</td><td></td></tr><tr><td>8</td><td>视频安防监控工作站</td><td>ThinkStation</td><td>台</td><td>1</td><td>联想</td><td></td></tr><tr><td>9</td><td>工作站</td><td>CE520F</td><td>台</td><td>5</td><td>浪潮</td><td></td></tr><tr><td>10</td><td>路由器</td><td>AR6280</td><td>台</td><td>1</td><td>华为</td><td></td></tr><tr><td>11</td><td>管理区核心交换机</td><td>S5731-H48T4XC</td><td>台</td><td>2</td><td>华为</td><td></td></tr><tr><td>12</td><td>管理信息区防火墙</td><td>USG-FW-310-T-NF1608</td><td>台</td><td>1</td><td>启明星辰</td><td></td></tr><tr><td>13</td><td>操作系统服务器版</td><td>服务器版（V10）</td><td>套</td><td>6</td><td>麒麟</td><td></td></tr><tr><td>14</td><td>操作系统工作站版</td><td>工作站版（V10）</td><td>套</td><td>6</td><td>麒麟</td><td></td></tr><tr><td>15</td><td>备品备件</td><td>国产</td><td>项</td><td>1</td><td>国产</td><td>含硬件安装调试</td></tr></table>";

    @Test
    public void simpleTest2(){
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(3);
        list.add(5);
        list.add(7);
        list.add(9);

        int key = 9;
        int index = Collections.binarySearch(list, key);

        if (index >= 0) {
            System.out.println("找到元素，索引位置: " + index);
        } else {
            System.out.println("未找到元素，可插入位置: " + (-index - 1));
        }
    }



    public  String convertHTMLTableToMarkdown(String htmlTable) {
        // 使用 Jsoup 解析 HTML
        Document doc = Jsoup.parse(htmlTable);
        Elements rows = doc.select("tr");

        if (rows.isEmpty()) {
            return "无法找到表格行";
        }

        StringBuilder markdownTable = new StringBuilder();

        // 处理表头
        Elements headerCells = rows.get(0).select("td");
        if (!headerCells.isEmpty()) {
            // 添加标题行
            markdownTable.append("| ");
            for (Element cell : headerCells) {
                markdownTable.append(cell.text()).append(" | ");
            }
            markdownTable.append("\n");

            // 添加分隔线
            markdownTable.append("|");
            for (int i = 0; i < headerCells.size(); i++) {
                markdownTable.append(" --- |");
            }
            markdownTable.append("\n");
        }

        // 处理数据行
        for (int i = headerCells.isEmpty() ? 0 : 1; i < rows.size(); i++) {
            Elements dataCells = rows.get(i).select("td");
            markdownTable.append("| ");

            for (Element cell : dataCells) {
                markdownTable.append(cell.text()).append(" | ");
            }
            markdownTable.append("\n");
        }

        return markdownTable.toString();
    }
}
