package com.did.docdiffserver.simple;

import com.github.difflib.DiffUtils;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.DeltaType;
import com.github.difflib.patch.Patch;
import lombok.Data;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DiffSimpleTest {


    @Test
    public void diffTest() {

//        String matchText = "1）质保期内，乙方提供 $7 \\times 2 4$ 小时电话技术支持服务；2）";
//
//        List<String> list = new ArrayList<>();
//        list.add("1）质保期内，乙方提供 $7 \\times 2 4$ 小时电话技术支持服务；");
//        list.add("2）乙方提供的设备有缺陷或质量问题需调试、维修、更换的，或者在用户使用过程中非因用户原因而发生损坏需调试、维修、更换的，乙方应在收到通知后立即进行免费的维修或更换。  ");


        String modifiedText = "1）质保期内，乙方提供 $7 \\times 2 4$ 小时电话技术支持服务；2）";

        String lastTwoStr = "1）质保期内，乙方提供 $7 \\times 2 4$ 小时电话技术支持服务；";

        String substring = lastTwoStr.substring(lastTwoStr.length() - 2);
        System.out.println("substring = " + substring);
        int index1 = modifiedText.indexOf(substring);

        String substring1 = modifiedText.substring(index1+2);
        System.out.println(substring1);



    }


    private void doDiff(String original, String revised) {
        List<String> originalChars = Arrays.asList(original.split(""));
        List<String> revisedChars = Arrays.asList(revised.split(""));

        Patch<String> patch = DiffUtils.diff(originalChars, revisedChars);

        for (AbstractDelta<String> delta : patch.getDeltas()) {
            DeltaType type = delta.getType();
            List<String> sourceLines = delta.getSource().getLines();
            List<String> targetLines = delta.getTarget().getLines();
            int position = delta.getSource().getPosition();

            int position1 = delta.getTarget().getPosition();

            System.out.println("类型: " + type);
            System.out.println("原始: " + sourceLines);
            System.out.println("原始位置: " + position);
            System.out.println("原始位置2: " + delta.getSource().getChangePosition());

            System.out.println("修改: " + targetLines);
            System.out.println("修改位置: " + position1);
            System.out.println("修改位置2: " + delta.getTarget().getChangePosition());
            System.out.println("-------");
        }

    }


}
