package ru.mirea.ippo.backend.test

import org.jxls.command.CellDataUpdater
import org.jxls.common.CellData
import org.jxls.common.CellRef
import org.jxls.common.Context

class TotalCellUpdater: CellDataUpdater {
    override fun updateCellData(cellData: CellData?, targetCell: CellRef?, context: Context?) {
        println(targetCell?.cellName)
        println(cellData?.cellValue)
        cellData?.cellComment = "TEst"
        cellData?.setEvaluationResult("TEST")
        println(cellData?.cellValue)
    }
}

data class Employee(
    val name: String = "TEST"
)