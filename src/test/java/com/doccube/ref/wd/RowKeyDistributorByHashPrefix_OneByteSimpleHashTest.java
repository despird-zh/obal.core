package com.doccube.ref.wd;

import com.doccube.core.hbase.ext.HashPrefixDistributor;

public class RowKeyDistributorByHashPrefix_OneByteSimpleHashTest extends RowKeyDistributorTestBase {
  public RowKeyDistributorByHashPrefix_OneByteSimpleHashTest() {
    super(new HashPrefixDistributor(new HashPrefixDistributor.OneByteSimpleHash(15)));
  }
}
