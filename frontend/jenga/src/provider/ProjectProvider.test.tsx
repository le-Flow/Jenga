// @vitest-environment jsdom
import { cleanup, render, waitFor } from "@solidjs/testing-library";
import { afterEach, describe, expect, test, vi } from "vitest";
import { useContext } from "solid-js";
import { AuthContext } from "./AuthProvider";
import { ProjectContext, ProjectProvider } from "./ProjectProvider";
import { ProjectResourceService, TicketPriority, TicketResourceService, TicketSize, TicketStatus } from "../api";

let projectCtx: ReturnType<typeof useContext<typeof ProjectContext>>;

const CaptureProjectContext = () => {
    projectCtx = useContext(ProjectContext);
    return null;
};

const renderProjectProvider = () =>
    render(() => (
        <AuthContext.Provider value={{ isLoggedIn: () => false } as never}>
            <ProjectProvider>
                <CaptureProjectContext />
            </ProjectProvider>
        </AuthContext.Provider>
    ));

afterEach(() => {
    cleanup();
    vi.restoreAllMocks();
});

describe("ProjectProvider", () => {
    test("updateTicket orchestrates ticket, acceptance criteria, and link API calls", async () => {
        const putApiTicketsSpy = vi.spyOn(TicketResourceService, "putApiTickets").mockResolvedValue({} as never);
        const getApiTicketsAcceptanceCriteriaSpy = vi
            .spyOn(TicketResourceService, "getApiTicketsAcceptanceCriteria")
            .mockResolvedValue([{ id: 10 }, { id: 11 }] as never);
        const deleteApiTicketsAcceptanceCriteriaSpy = vi
            .spyOn(TicketResourceService, "deleteApiTicketsAcceptanceCriteria")
            .mockResolvedValue(undefined as never);
        const postApiTicketsAcceptanceCriteriaSpy = vi
            .spyOn(TicketResourceService, "postApiTicketsAcceptanceCriteria")
            .mockResolvedValue({} as never);

        const getApiTickets1Spy = vi
            .spyOn(TicketResourceService, "getApiTickets1")
            .mockResolvedValueOnce({
                id: 1,
                relatedTicketsIds: [2, 3],
                blockingTicketIds: [4],
                blockedTicketIds: [6],
            } as never)
            .mockResolvedValueOnce({
                id: 1,
                relatedTicketsIds: [3, 5],
                blockingTicketIds: [4, 7],
                blockedTicketIds: [8],
            } as never);

        const deleteApiTicketsRelatedSpy = vi.spyOn(TicketResourceService, "deleteApiTicketsRelated").mockResolvedValue(undefined as never);
        const putApiTicketsRelatedSpy = vi.spyOn(TicketResourceService, "putApiTicketsRelated").mockResolvedValue(undefined as never);
        const deleteApiTicketsBlockSpy = vi.spyOn(TicketResourceService, "deleteApiTicketsBlock").mockResolvedValue(undefined as never);
        const putApiTicketsBlockSpy = vi.spyOn(TicketResourceService, "putApiTicketsBlock").mockResolvedValue(undefined as never);

        renderProjectProvider();

        await waitFor(() => {
            expect(typeof projectCtx?.updateTicket).toBe("function");
        });

        await projectCtx?.updateTicket("project-1", {
            id: 1,
            title: "Updated ticket",
            description: "Updated description",
            priority: TicketPriority.HIGH,
            size: TicketSize.LARGE,
            status: TicketStatus.IN_PROGRESS,
            assignee: "alice",
            labels: ["bug"],
            acceptanceCriteria: [
                { id: 21, description: "  AC 1  ", completed: true },
                { id: 22, description: "   ", completed: false },
            ],
            relatedTicketsIds: [3, 5, 5, 1, -1],
            blockingTicketIds: [4, 7, 1],
            blockedTicketIds: [8, 1],
        } as never);

        expect(putApiTicketsSpy).toHaveBeenCalledTimes(1);
        expect(putApiTicketsSpy).toHaveBeenCalledWith(1, expect.objectContaining({
            title: "Updated ticket",
            description: "Updated description",
        }));

        expect(getApiTicketsAcceptanceCriteriaSpy).toHaveBeenCalledWith(1);
        expect(deleteApiTicketsAcceptanceCriteriaSpy).toHaveBeenCalledTimes(2);
        expect(deleteApiTicketsAcceptanceCriteriaSpy).toHaveBeenNthCalledWith(1, 10, 1);
        expect(deleteApiTicketsAcceptanceCriteriaSpy).toHaveBeenNthCalledWith(2, 11, 1);
        expect(postApiTicketsAcceptanceCriteriaSpy).toHaveBeenCalledTimes(1);
        expect(postApiTicketsAcceptanceCriteriaSpy).toHaveBeenCalledWith(1, {
            description: "AC 1",
            completed: true,
        });

        expect(getApiTickets1Spy).toHaveBeenCalledTimes(2);
        expect(deleteApiTicketsRelatedSpy).toHaveBeenCalledWith(2, 1);
        expect(putApiTicketsRelatedSpy).toHaveBeenCalledWith(5, 1);

        expect(deleteApiTicketsBlockSpy).toHaveBeenCalledWith(1, 6);
        expect(putApiTicketsBlockSpy).toHaveBeenCalledWith(7, 1);
        expect(putApiTicketsBlockSpy).toHaveBeenCalledWith(1, 8);
    });

    test("deleteProject updates provider state and clears current project", async () => {
        const deleteProjectApiSpy = vi.spyOn(ProjectResourceService, "deleteApiProjects").mockResolvedValue(undefined as never);

        renderProjectProvider();

        await waitFor(() => {
            expect(typeof projectCtx?.deleteProject).toBe("function");
        });

        projectCtx?.setProjects([
            { identifier: "p1", name: "Project 1" },
            { identifier: "p2", name: "Project 2" },
        ] as never);
        projectCtx?.setSelectedProject({ identifier: "p1", name: "Project 1" } as never);
        projectCtx?.setTickets([{ id: 42, title: "Ticket" }] as never);

        await projectCtx?.deleteProject("p1");

        expect(deleteProjectApiSpy).toHaveBeenCalledWith("p1");
        expect(projectCtx?.projects()).toEqual([{ identifier: "p2", name: "Project 2" }]);
        expect(projectCtx?.selectedProject()).toBeUndefined();
        expect(projectCtx?.tickets()).toBeUndefined();
    });
});
