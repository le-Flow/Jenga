// @vitest-environment jsdom
import { cleanup, render, screen, waitFor } from "@solidjs/testing-library";
import userEvent from "@testing-library/user-event";
import { afterEach, describe, expect, test, vi } from "vitest";
import { Backlog } from "./Backlog";
import { createTestWrapper } from "../test/testProviders";

vi.mock("./NewTicketDialog", () => ({
    NewTicketDialog: (props: { open: boolean }) => <div data-open={String(props.open)} data-testid="new-ticket-dialog" />,
}));

afterEach(() => {
    cleanup();
    vi.restoreAllMocks();
});

describe("Backlog", () => {
    test("disables add button when no project is selected", () => {
        render(() => <Backlog tickets={[]} />, {
            wrapper: createTestWrapper(),
        });

        expect(screen.getByRole("button", { name: "add-ticket" })).toBeDisabled();
    });

    test("opens new-ticket dialog when add button is clicked", async () => {
        const user = userEvent.setup();

        render(() => <Backlog tickets={[]} />, {
            wrapper: createTestWrapper({ project: { selectedProject: () => ({ identifier: "project-1" }) } }),
        });

        await user.click(screen.getByRole("button", { name: "add-ticket" }));

        await waitFor(() => {
            expect(screen.getByTestId("new-ticket-dialog")).toHaveAttribute("data-open", "true");
        });
    });

    test("selects ticket when item is clicked", async () => {
        const user = userEvent.setup();
        const setSelectedTicket = vi.fn();

        render(
            () =>
                <Backlog
                    tickets={[
                        {
                            id: 7,
                            title: "Backlog Ticket",
                            reporter: "alice",
                            assignee: "bob",
                        },
                    ] as never}
                />,
            {
                wrapper: createTestWrapper({
                    project: {
                        selectedProject: () => ({ identifier: "project-1" }),
                        selectedTicket: () => undefined,
                        setSelectedTicket,
                    },
                }),
            },
        );

        await user.click(screen.getByRole("button", { name: "select-ticket-7" }));
        expect(setSelectedTicket).toHaveBeenCalledTimes(1);
    });
});
