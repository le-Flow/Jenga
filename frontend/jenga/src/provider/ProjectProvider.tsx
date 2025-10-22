import { createContext, Resource, createResource, JSXElement, createEffect, Accessor, Setter, createSignal } from "solid-js"
import { ProjectDTO, ProjectResourceService, TicketDTO, TicketResourceService } from "../api"

type ProjectContextType = {
    projects: Resource<ProjectDTO[]>

    selectedProject: Accessor<ProjectDTO>
    setSelectedProject: Setter<ProjectDTO>

    tickets: Resource<TicketDTO[]>
    setTickets: Setter<TicketDTO[]>
}

export const ProjectContext = createContext<ProjectContextType>()

interface ProviderProps {
    children: JSXElement
}

export const ProjectProvider = (props: ProviderProps) => {

    const [projects] = createResource(async () => await ProjectResourceService.getApiProjects())

    const [selectedProject, setSelectedProject] = createSignal<ProjectDTO>()
    
    const [tickets, {mutate}] = createResource(selectedProject, async (q)=> await TicketResourceService.getApiProjectsTickets(q.identifier))

    createEffect(()=> console.log(tickets()))

    const value = {
        projects: projects,
        selectedProject: selectedProject,
        setSelectedProject: setSelectedProject,
        tickets: tickets,
        setTickets: mutate
    }

    return (
        <ProjectContext.Provider value={value}>
            {props.children}
        </ProjectContext.Provider>
    )
}