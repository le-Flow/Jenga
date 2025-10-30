import { Accessor, JSXElement, Resource, Setter, createContext, createEffect, createResource, createSignal, useContext } from "solid-js";
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
    deleteProject: (identifier: string) => Promise<void>;
    updateTicket: (projectId: string, ticket: TicketDTO) => Promise<void>;
};

export const ProjectContext = createContext<ProjectContextType>();

interface ProviderProps {
    children: JSXElement;
}

export const ProjectProvider = (props: ProviderProps) => {

    const aCtx = useContext(AuthContext);

    const [selectedProject, setSelectedProject] = createSignal<ProjectDTO>();
    const [selectedTicket, setSelectedTicket] = createSignal<TicketDTO>();

    const [projects, { mutate: setProjects, refetch: refetchProjects }] = createResource(
        () => (aCtx?.isLoggedIn?.() ? true : undefined),
        async () => await ProjectResourceService.getApiProjects()
    );

    const [tickets, { mutate: setTickets, refetch: refetchTickets }] = createResource(
        () => {
            const project = selectedProject();
            return aCtx?.isLoggedIn() && project ? project.identifier : undefined;
        },
        async (projectId) => await TicketResourceService.getApiProjectsTickets(projectId)
    );

    createEffect(() => {
        if (!aCtx?.isLoggedIn?.()) {
            setProjects(() => undefined);
            setSelectedProject(undefined);
            setTickets(() => undefined);
            setSelectedTicket(undefined);
        }
    });

    const deleteProject = async (identifier: string) => {
        if (!identifier) return;

        try {
            await ProjectResourceService.deleteApiProjects(identifier);
            setProjects((prev) => prev?.filter((project) => project.identifier !== identifier));

            if (selectedProject()?.identifier === identifier) {
                setSelectedProject(undefined);
                setTickets(undefined);
            }
        } catch (error) {
            console.error("Failed to delete project", error);
            throw error;
        }
    };

    const updateTicket = async (projectId: string, ticket: TicketDTO) => {
        if (!projectId || ticket.id == null) {
            console.warn("Missing project or ticket id for update", { projectId, ticketId: ticket.id });
            return;
        }

        try {
            await TicketResourceService.putApiProjectsTickets(projectId, ticket.id, {...ticket, assignee: ticket.assigneeName});

            setTickets((prev) =>
                prev?.map((existing) => (existing.id === ticket.id ? { ...existing, ...ticket } : existing)) ?? prev
            );

            setSelectedTicket((prev) => (prev?.id === ticket.id ? { ...prev, ...ticket } : prev));
        } catch (error) {
            console.error("Failed to update ticket", error);
            throw error;
        }
    };

    const value = {
        projects,
        setProjects,
        selectedProject,
        setSelectedProject,
        tickets,
        setTickets,
        selectedTicket,
        setSelectedTicket,
        deleteProject,
        updateTicket
    };

    return (
        <ProjectContext.Provider value={value}>
            {props.children}
        </ProjectContext.Provider>
    );
};
