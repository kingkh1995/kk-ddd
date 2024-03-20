package com.kk.ddd.sales.web;

import com.kk.ddd.support.annotation.AccessCondition;
import com.kk.ddd.support.type.LongId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * <br>
 *
 * @author kingk
 */
@Component
@RequiredArgsConstructor
public class MockAppService {

  private final MockQueryService queryService;

  @AccessCondition(
      "!(1 < 2) || (#this.checkArgs(#_args) && @mockCheckPlugin.canAccess(#_result, 'test'))")
  public MockEntity find(LongId id) {
    var optional = queryService.find(id);
    return optional.get();
  }
}
