import { createContext, Resource, createResource, JSXElement } from "solid-js"
import { ProjectDTO, ProjectResourceService } from "../api"

type ProjectContextType = {
    projects: Resource<ProjectDTO[]>
}

export const ProjectContext = createContext<ProjectContextType>()

interface ProviderProps {
    children: JSXElement
}

export const ProjectProvider = (props: ProviderProps) => {

    const [projects] = createResource(async () => await ProjectResourceService.getApiProjects())

    const value = {
        projects: projects
    }

    return (
        <ProjectContext.Provider value={value}>
            {props.children}
        </ProjectContext.Provider>
    )
}