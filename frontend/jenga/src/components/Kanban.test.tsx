// @vitest-environment jsdom
import { cleanup, fireEvent, render, screen, waitFor, within } from "@solidjs/testing-library";
import userEvent from "@testing-library/user-event";
import { afterEach, describe, expect, test, vi } from "vitest";
import { TicketStatus } from "../api";
import { Kanban } from "./Kanban";
import { createTestWrapper } from "../test/testProviders";

afterEach(() => {
    cleanup();
    vi.restoreAllMocks();
});

describe("Kanban", () => {
    test("moves ticket on drop and updates status", async () => {
        const updateTicket = vi.fn().mockResolvedValue(undefined);
        const tickets = [{ id: 42, title: "Ticket 42", assignee: "alice", status: TicketStatus.OPEN, labels: [] }];

        render(() => <Kanban tickets={tickets as never} />, {
            wrapper: createTestWrapper({
                project: {
                    selectedProject: () => ({ identifier: "project-1" }),
                    tickets: (() => tickets) as never,
                    updateTicket,
                    setSelectedTicket: vi.fn(),
                    selectedTicket: () => undefined,
                },
            }),
        });

        const row = screen.getByText("alice").closest("tr") as HTMLTableRowElement;
        const inProgressCell = within(row).getAllByRole("cell")[2] as HTMLElement;

        await fireEvent.drop(inProgressCell, {
            dataTransfer: { getData: () => "42" },
        });

        await waitFor(() => {
            expect(updateTicket).toHaveBeenCalledTimes(1);
            const [, updatedTicket] = updateTicket.mock.calls[0];
            expect(updatedTicket.status).toBe(TicketStatus.IN_PROGRESS);
            expect(updatedTicket.assignee).toBe("alice");
        });
    });

    test("selects ticket when clicked", async () => {
        const user = userEvent.setup();
        const setSelectedTicket = vi.fn();
        const tickets = [{ id: 42, title: "Ticket 42", assignee: "alice", status: TicketStatus.OPEN, labels: [] }];

        render(() => <Kanban tickets={tickets as never} />, {
            wrapper: createTestWrapper({
                project: {
                    selectedProject: () => ({ identifier: "project-1" }),
                    selectedTicket: () => undefined,
                    setSelectedTicket,
                },
            }),
        });

        await user.click(screen.getByText("Ticket 42"));
        expect(setSelectedTicket).toHaveBeenCalled();
    });

    test("ignores invalid drag payload", async () => {
        const updateTicket = vi.fn().mockResolvedValue(undefined);
        const tickets = [{ id: 42, title: "Ticket 42", assignee: "alice", status: TicketStatus.OPEN, labels: [] }];

        render(() => <Kanban tickets={tickets as never} />, {
            wrapper: createTestWrapper({
                project: {
                    selectedProject: () => ({ identifier: "project-1" }),
                    tickets: (() => tickets) as never,
                    updateTicket,
                    setSelectedTicket: vi.fn(),
                    selectedTicket: () => undefined,
                },
            }),
        });

        const row = screen.getByText("alice").closest("tr") as HTMLTableRowElement;
        const inProgressCell = within(row).getAllByRole("cell")[2] as HTMLElement;

        await fireEvent.drop(inProgressCell, {
            dataTransfer: { getData: () => "not-a-number" },
        });

        expect(updateTicket).not.toHaveBeenCalled();
    });
});
