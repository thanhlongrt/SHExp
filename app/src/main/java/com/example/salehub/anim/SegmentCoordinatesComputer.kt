package com.example.salehub.anim

import kotlin.math.max

class SegmentCoordinatesComputer {

    fun segmentCoordinates(
        position: Int,
        segmentCount: Int,
        width: Float,
        spacing: Float
    ): SegmentCoordinates {
        val segmentWidth = (width - spacing * (segmentCount - 1)) / segmentCount
        val isLast = position == segmentCount - 1

        val topLeft = (segmentWidth + spacing) * position
        val bottomLeft = (segmentWidth + spacing) * position
        val topRight = segmentWidth * (position + 1) + spacing * position
        val bottomRight = segmentWidth * (position + 1) + spacing * position

        return SegmentCoordinates(topLeft, topRight, bottomLeft, bottomRight)
    }

    fun progressCoordinates(
        progress: Int,
        segmentCount: Int,
        width: Float,
        spacing: Float
    ): SegmentCoordinates {
        val segmentWidth = (width - spacing * (segmentCount - 1)) / segmentCount
        val isLast = progress == segmentCount

        val topRight = segmentWidth * progress + spacing * max(0, progress - 1)
        val bottomRight = segmentWidth * progress + spacing * max(0, progress - 1)

        return SegmentCoordinates(0f, topRight, 0f, bottomRight)
    }

}