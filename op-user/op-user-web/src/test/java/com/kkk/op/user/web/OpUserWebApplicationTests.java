package com.kkk.op.user.web;

import com.kkk.op.support.tools.SearchUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class OpUserWebApplicationTests {

  @Test
  void test() {
    var list = new ArrayList<Integer>(200000);
    for (int i = 0; i < 200000; i++) {
      list.add(ThreadLocalRandom.current().nextInt(0, 1000000));
    }
    Collections.sort(list);
    System.out.println(Collections.binarySearch(list, 666666, Comparator.naturalOrder()));
    System.out.println(SearchUtil.fibSearch(list, 666666, Comparator.naturalOrder()));
    System.out.println(SearchUtil.ceil(list, 666666, Comparator.naturalOrder()));
    System.out.println(SearchUtil.floor(list, 666666, Comparator.naturalOrder()));
  }
}
