package com.dcube.ref.wd;

import com.dcube.core.hbase.ext.HashPrefixDistributor;

public class RowKeyDistributorByHashPrefix_OneByteSimpleHashTest extends RowKeyDistributorTestBase {
  public RowKeyDistributorByHashPrefix_OneByteSimpleHashTest() {
    super(new HashPrefixDistributor(new HashPrefixDistributor.OneByteSimpleHash(15)));
  }
}
