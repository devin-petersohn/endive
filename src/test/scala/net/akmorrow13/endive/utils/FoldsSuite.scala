package net.akmorrow13.endive.utils

import net.akmorrow13.endive.EndiveFunSuite
import net.akmorrow13.endive.processing.PeakRecord
import org.bdgenomics.adam.models.ReferenceRegion

class FoldsSuite extends EndiveFunSuite {

  // training data of region and labels
  var labelPath = resourcePath("ARID3A.train.labels.head30.tsv")


  sparkTest("Leave 1 out (test set is 1 cell type/chromsome pair)") {
    val NUM_CELL_TYPES = 4
    val NUM_CHROMOSOMES = 23
    val NUM_SAMPLES = 100
    val CELL_TYPES_PER_FOLD = 1
    val CHROMOSOMES_PER_FOLD = 1
    val cellType0 = "cellType0"
    val cellType1 = "cellType1"
    val region0 = ReferenceRegion("chr0", 0, 100)
    val region1 = ReferenceRegion("chr1", 0, 100)
    val sequence = "ATTTTGGGGGAAAAA"
    val tf = "ATF3"


    val windowsAll  = (0 until NUM_SAMPLES).map { x =>
      ((0 until NUM_CHROMOSOMES).map { cr =>
        (0 until NUM_CELL_TYPES).map { cellType =>
        LabeledWindow(Window(tf, cellType.toString, ReferenceRegion("chr" + cr, 0, 100), sequence, None, None), 0)
        }
      }).flatten
    }
    val windows:Seq[LabeledWindow] = windowsAll.flatten
    val windowsRDD = sc.parallelize(windows)

    /* First one chromesome and one celltype per fold (leave 1 out) */
    val folds = EndiveUtils.generateFoldsRDD(windowsRDD, CELL_TYPES_PER_FOLD, CHROMOSOMES_PER_FOLD, 1)
    val cellTypesChromosomes:Iterable[(String, String)] = windowsRDD.map(x => (x.win.getRegion.referenceName, x.win.cellType)).countByValue().keys

    println("TOTAL FOLDS " + folds.size)
    for (i <- (0 until folds.size)) {
      println("FOLD " + i)
      val train = folds(i)._1
      val test = folds(i)._2

      println("TRAIN SIZE IS " + train.count())
      println("TEST SIZE IS " + test.count())

      val cellTypesTest:Iterable[String] = test.map(x => (x.win.cellType)).countByValue().keys
      val chromosomesTest:Iterable[String] = test.map(x => (x.win.getRegion.referenceName)).countByValue().keys
      val cellTypesChromosomesTest:Iterable[(String, String)] = test.map(x => (x.win.getRegion.referenceName, x.win.cellType)).countByValue().keys
      println(cellTypesTest.size)
      println(chromosomesTest.size)

      assert(cellTypesTest.size == CELL_TYPES_PER_FOLD)
      assert(chromosomesTest.size == CHROMOSOMES_PER_FOLD)
      val cellTypesTrain:Iterable[String] = train.map(x => (x.win.cellType)).countByValue().keys
      val chromosomesTrain:Iterable[String] = train.map(x => (x.win.getRegion.referenceName)).countByValue().keys

      assert(cellTypesTrain.size == NUM_CELL_TYPES - CELL_TYPES_PER_FOLD)
      assert(chromosomesTrain.size == NUM_CHROMOSOMES - CHROMOSOMES_PER_FOLD)
    }
  }

  sparkTest("Leave 3 out (test set is 3 cell type/chromsome pair)") {
    val NUM_CELL_TYPES = 10
    val NUM_CHROMOSOMES = 23
    val NUM_SAMPLES = 100
    val CELL_TYPES_PER_FOLD = 3
    val CHROMOSOMES_PER_FOLD = 3
    val cellType0 = "cellType0"
    val cellType1 = "cellType1"
    val region0 = ReferenceRegion("chr0", 0, 100)
    val region1 = ReferenceRegion("chr1", 0, 100)
    val sequence = "ATTTTGGGGGAAAAA"
    val tf = "ATF3"


    val windowsAll  = (0 until NUM_SAMPLES).map { x =>
      ((0 until NUM_CHROMOSOMES).map { cr =>
        (0 until NUM_CELL_TYPES).map { cellType =>
        LabeledWindow(Window(tf, cellType.toString, ReferenceRegion("chr" + cr, 0, 100), sequence, None, None), 0)
        }
      }).flatten
    }
    val windows:Seq[LabeledWindow] = windowsAll.flatten
    val windowsRDD = sc.parallelize(windows)

    /* First one chromesome and one celltype per fold (leave 1 out) */
    val folds = EndiveUtils.generateFoldsRDD(windowsRDD, CELL_TYPES_PER_FOLD, CHROMOSOMES_PER_FOLD, 1)
    val cellTypesChromosomes:Iterable[(String, String)] = windowsRDD.map(x => (x.win.getRegion.referenceName, x.win.cellType)).countByValue().keys

    println("TOTAL FOLDS " + folds.size)
    for (i <- (0 until folds.size)) {
      println("FOLD " + i)
      val train = folds(i)._1
      val test = folds(i)._2

      println("TRAIN SIZE IS " + train.count())
      println("TEST SIZE IS " + test.count())

      val cellTypesTest:Iterable[String] = test.map(x => (x.win.cellType)).countByValue().keys
      val chromosomesTest:Iterable[String] = test.map(x => (x.win.getRegion.referenceName)).countByValue().keys
      val cellTypesChromosomesTest:Iterable[(String, String)] = test.map(x => (x.win.getRegion.referenceName, x.win.cellType)).countByValue().keys
      println(cellTypesTest.size)
      println(chromosomesTest.size)

      assert(cellTypesTest.size == CELL_TYPES_PER_FOLD)
      assert(chromosomesTest.size == CHROMOSOMES_PER_FOLD)
      val cellTypesTrain:Iterable[String] = train.map(x => (x.win.cellType)).countByValue().keys
      val chromosomesTrain:Iterable[String] = train.map(x => (x.win.getRegion.referenceName)).countByValue().keys

      assert(cellTypesTrain.size == NUM_CELL_TYPES - CELL_TYPES_PER_FOLD)
      assert(chromosomesTrain.size == NUM_CHROMOSOMES - CHROMOSOMES_PER_FOLD)
    }
  }

}
