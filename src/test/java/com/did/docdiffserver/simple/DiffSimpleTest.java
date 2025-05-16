package com.did.docdiffserver.simple;

import com.github.difflib.DiffUtils;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.DeltaType;
import com.github.difflib.patch.Patch;
import lombok.Data;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class DiffSimpleTest {



    @Test
    public void diffTest() {
        String original = "天中国人民站起来了";
        String revised = "";
        doDiff(original, revised);
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

            System.out.println("修改: " + targetLines);
            System.out.println("修改位置: " + position1);
            System.out.println("-------");
        }

    }


}
