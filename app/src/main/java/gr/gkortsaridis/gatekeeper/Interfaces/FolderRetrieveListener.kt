package gr.gkortsaridis.gatekeeper.Interfaces

import gr.gkortsaridis.gatekeeper.Entities.Folder

interface FolderRetrieveListener {
    fun onFoldersRetrieveSuccess(folders: ArrayList<Folder>)
    fun onFoldersRetrieveError(e: Exception)
}

interface FolderCreateListener {
    fun onFolderCreated()
    fun onFolderCreateError()
}

interface FolderSetupListener {
    fun onFolderSetupComplete()
    fun onFolderSetupError()
}