import { Accessor, JSXElement, Resource, Setter, createContext, createResource, createSignal, useContext } from "solid-js";
import { ProjectDTO, ProjectResourceService, TicketDTO, TicketResourceService } from "../api";
import { AuthContext } from "./AuthProvider";

type ProjectContextType = {
    projects: Resource<ProjectDTO[] | undefined>;
    setProjects: Setter<ProjectDTO[] | undefined>;

    selectedProject: Accessor<ProjectDTO | undefined>;
    setSelectedProject: Setter<ProjectDTO | undefined>;

    tickets: Resource<TicketDTO[] | undefined>;
    setTickets: Setter<TicketDTO[] | undefined>;

    selectedTicket: Accessor<TicketDTO | undefined>;
    setSelectedTicket: Setter<TicketDTO | undefined>;
};

export const ProjectContext = createContext<ProjectContextType>();

interface ProviderProps {
    children: JSXElement;
}

export const ProjectProvider = (props: ProviderProps) => {

    const aCtx = useContext(AuthContext);

    const [selectedProject, setSelectedProject] = createSignal<ProjectDTO>();
    const [selectedTicket, setSelectedTicket] = createSignal<TicketDTO>();

    const [projects, { mutate: setProjects }] = createResource(
        () => (aCtx?.isLoggedIn() ? true : undefined),
        async () => await ProjectResourceService.getApiProjects()
    );

    const [tickets, { mutate: setTickets }] = createResource(
        () => {
            const project = selectedProject();
            return aCtx?.isLoggedIn() && project ? project.identifier : undefined;
        },
        async (projectId) => await TicketResourceService.getApiProjectsTickets(projectId)
    );

    const value = {
        projects,
        setProjects,
        selectedProject,
        setSelectedProject,
        tickets,
        setTickets,
        selectedTicket,
        setSelectedTicket
    };

    return (
        <ProjectContext.Provider value={value}>
            {props.children}
        </ProjectContext.Provider>
    );
};
