package com.kkk.op.support.changeTracking.diff;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author KaiKoo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Diff<T> {

    private DiffType type;

}
