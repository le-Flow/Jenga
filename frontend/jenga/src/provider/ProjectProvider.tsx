import { createContext, Resource, createResource, JSXElement, createEffect, Accessor, Setter, createSignal, useContext, on } from "solid-js"
import { ProjectDTO, ProjectResourceService, TicketDTO, TicketResourceService } from "../api"
import { UserContext } from "./UserProvider"

type ProjectContextType = {
    projects: Resource<ProjectDTO[]>
    setProjects: Setter<ProjectDTO[]>

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

    const uCtx = useContext(UserContext)

    const [projects, { mutate: setProjects, refetch }] = createResource(async () => await ProjectResourceService.getApiProjects())

    const [selectedProject, setSelectedProject] = createSignal<ProjectDTO>()

    const [tickets, { mutate: setTickets }] = createResource(selectedProject, async (q) => await TicketResourceService.getApiProjectsTickets(q.identifier))

    createEffect(() => console.log(tickets()))
    createEffect(()=>{
        if(uCtx?.isLoggedIn()){
            console.log("refetch") 
            refetch()
        }
    })

    const value = {
        projects: projects,
        setProjects: setProjects,
        selectedProject: selectedProject,
        setSelectedProject: setSelectedProject,
        tickets: tickets,
        setTickets: setTickets
    }

    return (
        <ProjectContext.Provider value={value}>
            {props.children}
        </ProjectContext.Provider>
    )
}