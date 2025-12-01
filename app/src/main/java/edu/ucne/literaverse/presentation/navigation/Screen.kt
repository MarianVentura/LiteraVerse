package edu.ucne.literaverse.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable
    data object Welcome : Screen

    @Serializable
    data object Login : Screen

    @Serializable
    data object Register : Screen

    @Serializable
    data object Home : Screen


    @Serializable
    data object Search : Screen

    @Serializable
    data object Library : Screen

    @Serializable
    data object Write : Screen

    @Serializable
    data object Perfil : Screen

    @Serializable
    data object MyStories : Screen

    @Serializable
    data object CreateStory : Screen

    @Serializable
    data class StoryDetail(val storyId: Int) : Screen

    @Serializable
    data class StoryDetailReader(val storyId: Int) : Screen

    @Serializable
    data class ChapterEditor(val storyId: Int, val chapterId: Int) : Screen

    @Serializable
    data class CreateChapter(val storyId: Int) : Screen


}